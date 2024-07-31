package com.example.daytastic

import android.annotation.SuppressLint
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
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var title: TextView
    private lateinit var temp: TextView

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
        initWidgets()
    }

    private fun initWidgets() {
        title = findViewById(R.id.headerTextView)
        temp = findViewById(R.id.tempTextView)
        getTemperature(temp, TemperatureEngine(), "Tel-Aviv")
    }

    @SuppressLint("SetTextI18n")
    private fun getTemperature(textView: TextView, temperatureEngine: TemperatureEngine, location:String){
        Log.d("init","getting temperature")
        try {
            val future = temperatureEngine.getWeather(location)
            future.whenComplete { weather: Weather?, e: Throwable? ->
                runOnUiThread {
                    if (future.isCompletedExceptionally)
                        textView.text = e!!.message
                    else
                        textView.text = weather!!.days[0].aTemp.toString()+"°C"
                }
            }
        }catch (ignore:Exception){
            Log.d("OKHTTP3","Failed to get temperature.. internet connection?")
        }
    }

    fun testButton(view: View){
        Log.d("Interactions","Test Button Pressed")
    }
}