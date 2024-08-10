package com.example.daytastic.weather

import com.example.daytastic.R
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
        fun getImages(condition: String, time:String):Int{
            return when(condition){
                "Cloudy","Partly cloudy","Mist","Fog","Freezing fog","Overcast" -> {if (time!="night") R.drawable.cloudy_day else R.drawable.cloudy_night}
                "Sunny" -> R.drawable.sunny
                "Patchy rain possible","Heavy rain at time","Moderate rain at times","Patchy light rain","Patchy light drizzle" -> R.drawable.partly_light_rain
                "Clear" -> R.drawable.night
                "Moderate or heavy rain shower","Torrential rain shower","Moderate or heavy freezing rain","Heavy rain"
                    ,"Rainy","Moderate rain" -> R.drawable.rainy
                "Light rain shower","Light freezing rain","Light rain","Heavy freezing drizzle"
                    ,"Freezing drizzle","Light drizzle" -> R.drawable.light_rain
                "Thundery outbreaks possible","Patchy light rain with thunder","Moderate or heavy rain with thunder" -> R.drawable.stormy
                "Patchy light snow with thunder","Moderate or heavy snow with thunder","Moderate or heavy showers of ice pellets"
                    ,"Light showers of ice pellets","Moderate or heavy snow showers","Light snow showers","Moderate or heavy sleet showers","Light sleet showers"
                    ,"Ice pellets","Heavy snow","Patchy heavy snow","Moderate snow","Patchy moderate snow","Light snow","Patchy light snow"
                    ,"Moderate or heavy sleet","Light sleet","Blizzard","Blowing snow"-> R.drawable.snow
                else -> R.drawable.sunny
            }
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