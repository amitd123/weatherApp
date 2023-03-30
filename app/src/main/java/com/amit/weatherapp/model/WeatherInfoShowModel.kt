package com.amit.weatherapp.model

import com.amit.weatherapp.common.RequestCompleteListener
import com.amit.weatherapp.model.data_class.City
import com.amit.weatherapp.model.data_class.WeatherInfoResponse

interface WeatherInfoShowModel {
    fun getCityList(callback: RequestCompleteListener<MutableList<City>>)
    fun getWeatherInfo(lat : String, long : String, callback: RequestCompleteListener<WeatherInfoResponse>)
}