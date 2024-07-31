package com.example.daytastic.weather

import com.example.daytastic.weather.forecast.Forecast
import com.example.daytastic.weather.forecast.Forecastday

class WeatherUtils {
    companion object {
        fun getHourlyTemp(data: Forecastday): Array<HourData> {
            var res = emptyArray<HourData>()
            for (hour in data.hour) {

                res += HourData(hour.tempC!!,hour.chanceOfRain!!, hour.condition!!.text!!)
            }
            return res
        }
        fun getDayData(data: Forecast): Array<DayData> {
            var res = emptyArray<DayData>()
            for (day in data.forecastday) {
                res += DayData(day)
            }
            return res
        }

        fun conditionToImg(condition:String): String{
            var res = ""
            return res
        }

    }

}