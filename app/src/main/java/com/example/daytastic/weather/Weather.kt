package com.example.daytastic.weather

import android.util.Log
import com.example.daytastic.forecast.ForecastData
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

class Weather(data: ForecastData){
    val days = WeatherUtils.getDayData(data.forecast!!)
    val currentTemp = data.current!!.tempC!!.roundToInt()
    val currentHumidity = data.current!!.humidity!!
    val currentHour = getHour(data.location!!.localtime!!)
    val currentRainChance = days[0].hourlyTemp[currentHour].chanceOfRain.toInt()
    val currentCondition = data.current!!.condition!!.text!!

    private fun getHour(date:String): Int{
        val hour = date.split(" ")[1].split(":")[0].toInt()
        return hour
    }


}
