package com.amit.weatherapp.data.repositories

import android.net.Uri
import com.amit.weatherapp.data.firebase.FirebaseSource

class UserRepository (
    private val firebase: FirebaseSource
){
    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String, username: String, bio: String, uri: Uri) = firebase.register(email, password,username,bio,uri)

    fun currentUser() = firebase.currentUser()

    fun logout() = firebase.logout()
}