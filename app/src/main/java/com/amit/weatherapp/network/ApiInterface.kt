package com.amit.weatherapp.network

import com.amit.weatherapp.model.data_class.WeatherInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun callApiForWeatherInfo(@Query("lat") lat: String,@Query("lon") lon: String): Call<WeatherInfoResponse>

}