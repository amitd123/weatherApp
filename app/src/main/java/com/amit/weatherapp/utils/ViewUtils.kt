package com.amit.weatherapp.utils

import android.content.Context
import android.content.Intent
import com.amit.weatherapp.ui.auth.LoginActivity
import com.amit.weatherapp.ui.home.HomeActivity
import com.amit.weatherapp.ui.home.WeatherActivity

fun Context.startHomeActivity() =
    Intent(this, HomeActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startLoginActivity() =
    Intent(this, LoginActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun Context.startWeatherActivity() =
    Intent(this, WeatherActivity::class.java).also {
        startActivity(it)
    }