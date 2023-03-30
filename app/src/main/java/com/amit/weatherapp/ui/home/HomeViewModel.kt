package com.amit.weatherapp.ui.home

import android.view.View
import androidx.lifecycle.ViewModel
import com.amit.weatherapp.data.repositories.UserRepository
import com.amit.weatherapp.utils.startLoginActivity
import com.amit.weatherapp.utils.startWeatherActivity

class HomeViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val user by lazy {
        repository.currentUser()
    }

    fun getDataFromDatabase(uid:String){
        repository.currentUser()
    }
    
    fun logout(view: View){
        repository.logout()
        view.context.startLoginActivity()
    }

    fun weatherActivity(view: View){
        repository.logout()
        view.context.startWeatherActivity()
    }
}