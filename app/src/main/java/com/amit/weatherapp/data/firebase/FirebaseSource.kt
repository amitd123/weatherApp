package com.amit.weatherapp.data.firebase

import android.net.Uri
import android.util.Log
import com.amit.weatherapp.model.data_class.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable
import io.reactivex.CompletableEmitter

class FirebaseSource {

     var firebaseDatabase : DatabaseReference? = null
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseDatabaseInstances: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }



    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String, username: String, bio: String, uri: Uri) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    Log.d("createUserWithEmailAndPassword","Successful")
//                    val signUpUser: User = User(username, email, password, bio, "")
//                    firebaseDatabase = firebaseDatabaseInstances.reference
//                    //User data change Listener
//                    firebaseDatabase!!.child("users").child(it!!.result.user!!.uid).child("userdata").setValue(signUpUser).addOnCompleteListener({
//                        if (it.isSuccessful) {
//                            Log.d("createUserWithEmailAndPassword","database insertion sucessfullSuccessful")
//                            emitter.onComplete()
//                        } else {
//                            it.exception?.let { it1 ->
//                                emitter.onError(it1)
//                            }
//                        }
//                    })

                    uploadImage(email,password,username,bio,uri,it.result.user!!.uid +".jpg", it.result.user!!.uid.toString(),emitter)
//                    emitter.onComplete()
                } else {
                    Log.d("createUserWithEmailAndPassword","auth failure")
                    emitter.onError(it.exception!!)
                }
            }
        }
    }

    private fun uploadImage(
        email: String,
        password: String,
        username: String,
        bio: String,
        uri: Uri,
        fileName: String,
        uuid: String,
        emitter: CompletableEmitter
    ) {
//        val database = FirebaseDatabase.getInstance()
        Log.d("createUserWithEmailAndPassword","image upload start")
        val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
        refStorage.putFile(uri)
            .addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        Log.d("createUserWithEmailAndPassword","image upload success")
                        val signUpUser: User = User(username, email, password, bio, it.toString())
                        firebaseDatabase = firebaseDatabaseInstances.getReference("users")
                        //User data change Listener
                        firebaseDatabase!!.child(uuid).setValue(signUpUser).addOnCompleteListener({
                            if (it.isSuccessful){
                                Log.d("createUserWithEmailAndPassword","database insertion success")
                                emitter.onComplete()
                            }else{
                                Log.d("createUserWithEmailAndPassword","database insertion failure +"+it.exception)
                                it.exception?.let { it1 -> emitter.onError(it1) }
                            }
                        })
//                        val user=FirebaseAuth.getInstance().currentUser
//                        val signUpUser:User = User(username,email,password,bio, it.toString())
//                                firebaseDatabase =firebaseDatabaseInstances.getReference("users")
//                                //User data change Listener
//                                firebaseDatabase!!.child(user!!.uid).addValueEventListener(object:  ValueEventListener {
//                            override fun onDataChange(dataSnapshot: DataSnapshot){
//                                val user = dataSnapshot.getValue(User::class.java)
//                                emitter.onComplete()
//                            }
//                            override fun onCancelled(error: DatabaseError){
//                                //Failed to read value
////                                Log.e(TAG,"Failed to read user",error.toException())
//                                emitter.onError(error.toException())
//                            }
//                        })
                    }
                })

            ?.addOnFailureListener(OnFailureListener { e ->
                print(e.message)
                Log.d("createUserWithEmailAndPassword","image upload failure")
                emitter.onError(e)
            })
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser


/*
    fun getDataFromDatabase(uuid: String)= Completable.create { emitter ->
                            firebaseDatabase = firebaseDatabaseInstances.getReference("users").child(uuid)
                            //User data change Listener
                            firebaseDatabase.get().result
                                .addOnCompleteListener({
                                    if (it.isSuccessful) {
                                        Log.d(
                                            "createUserWithEmailAndPassword",
                                            "database insertion success"
                                        )
                                        emitter.onComplete()
                                    } else {
                                        Log.d(
                                            "createUserWithEmailAndPassword",
                                            "database insertion failure +" + it.exception
                                        )
                                        it.exception?.let { it1 -> emitter.onError(it1) }
                                    }
                                })
//                        val user=FirebaseAuth.getInstance().currentUser
//                        val signUpUser:User = User(username,email,password,bio, it.toString())
//                                firebaseDatabase =firebaseDatabaseInstances.getReference("users")
//                                //User data change Listener
//                                firebaseDatabase!!.child(user!!.uid).addValueEventListener(object:  ValueEventListener {
//                            override fun onDataChange(dataSnapshot: DataSnapshot){
//                                val user = dataSnapshot.getValue(User::class.java)
//                                emitter.onComplete()
//                            }
//                            override fun onCancelled(error: DatabaseError){
//                                //Failed to read value
////                                Log.e(TAG,"Failed to read user",error.toException())
//                                emitter.onError(error.toException())
//                            }
//                        })
                        }
*/
//        }


}