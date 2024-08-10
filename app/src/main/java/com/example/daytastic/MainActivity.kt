package com.example.daytastic

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.daytastic.databinding.ActivityMainBinding
import com.example.daytastic.weather.Weather
import com.example.daytastic.weather.WeatherInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import java.time.LocalDate


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_calender, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        initWeather()
    }

    private fun initWeather() {
        val pref: SharedPreferences = getSharedPreferences("weather",0)
        val weatherString = pref.getString("weather",null)
        if(weatherString!=null){
            val weather = Gson().fromJson(weatherString,Weather::class.java)
            val date = LocalDate.now().toString().split(" ")[0]

            if(weather.days[0].date.split(" ")[0] == date){
                WeatherInstance.weather = weather
                return
            }
        }
        getTemperature(TemperatureEngine(),"Tel-Aviv")
    }

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    private fun getTemperature(temperatureEngine: TemperatureEngine, location:String){
        Log.d("init","getting temperature")
        try {
            val future = temperatureEngine.getWeather(location)
            future.whenComplete { weather: Weather?, e: Throwable? ->
                    if (!future.isCompletedExceptionally){
                        WeatherInstance.weather = weather
                        val json = Gson().toJson(WeatherInstance.weather)
                        val pref: SharedPreferences = getSharedPreferences("weather",0)
                        pref.edit().putString("weather",json)
                    }
            }
        }catch (ignore:Exception){
            Log.d("OKHTTP3","Failed to get temperature.. internet connection?")
        }
    }

}