package com.example.daytastic.weather


import com.example.daytastic.weather.forecast.Forecastday
import kotlin.math.round
import kotlin.math.roundToInt


class DayData(data: Forecastday){
    val date = data.date!!
    val aTemp = data.day!!.avgtempC!!.roundToInt()
    val rainChance = data.day!!.dailyChanceOfRain!!
    val hourlyTemp = WeatherUtils.getHourlyTemp(data)
    val condition = data.day!!.condition!!.text!!
}
