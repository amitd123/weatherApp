package com.amit.weatherapp.ui.auth

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.amit.weatherapp.BuildConfig
import com.amit.weatherapp.R
import com.amit.weatherapp.databinding.ActivitySignupBinding
import com.amit.weatherapp.ui.home.HomeActivity
import com.amit.weatherapp.utils.startHomeActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class SignupActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory : AuthViewModelFactory by instance()
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivitySignupBinding
    var myBitmap: Bitmap? = null
    var picUri: Uri? = null

    private val GALLERY = 1
    private val CAMERA = 2
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    private val ALL_PERMISSIONS_RESULT = 107

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.amit.weatherapp.R.layout.activity_signup)
        binding = DataBindingUtil.setContentView(this, com.amit.weatherapp.R.layout.activity_signup)

        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this

        binding.userProfilePhoto.setOnClickListener({
            showPictureDialog(it)
        })
    }

    override fun onStarted() {
        binding.progressbar.visibility = View.VISIBLE
        Intent(this, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onSuccess() {
        binding.progressbar.visibility = View.GONE
        startHomeActivity()
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showPictureDialog(v:View) {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        }else {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY)
        }
    }

    private fun takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        }else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        }else{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveTempBitmap(bitmap,this)
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
                    binding.userProfilePhoto.setImageBitmap(bitmap)
                    viewModel.imgUri = path
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            binding.userProfilePhoto.setImageBitmap(thumbnail)
            val path =  saveTempBitmap(thumbnail,this)
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
            viewModel.imgUri = path
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
//            f.createNewFile()
            if (!f.getParentFile()?.exists()!!)
                f.getParentFile()?.mkdirs();
            if (!f.exists())
                f.createNewFile();
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    fun saveTempBitmap(selectedBitmapImage: Bitmap, context: Context): Uri? {
        var mUriImage: Uri? = null
        try {
            val capturedImage = File(context.externalCacheDir, "temp_post_img")
            if (capturedImage.exists()) {
                capturedImage.delete()
            }
            capturedImage.createNewFile()
            mUriImage = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", capturedImage)
            } else {
                Uri.fromFile(capturedImage)
            }
            try {
                val out = FileOutputStream(capturedImage)
                selectedBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                return mUriImage
            } catch (e: java.lang.Exception) {
//                exceptionHandling(e, "")
            }
        } catch (e: Exception) {
//            exceptionHandling(e, "")
        }
        return mUriImage
    }


    companion object {
        private val IMAGE_DIRECTORY = "/userprofile"
    }

}
