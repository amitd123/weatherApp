package com.amit.weatherapp.model.data_class

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
        @SerializedName("City")
        val city: String = "",
        @SerializedName("Lat")
        val lat: String = "",
        @SerializedName("Long")
        val long: String = ""
): Serializable