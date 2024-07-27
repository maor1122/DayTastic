package com.example.daytastic.forecast

import com.google.gson.annotations.SerializedName


data class ForecastData (

    @SerializedName("location" ) var location : Location? = Location(),
    @SerializedName("current"  ) var current  : Current?  = Current(),
    @SerializedName("forecast" ) var forecast : Forecast? = Forecast()

)