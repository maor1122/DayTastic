package com.example.daytastic.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment 
import com.example.daytastic.databinding.FragmentHomeBinding
import com.example.daytastic.weather.WeatherInstance.weather
import com.example.daytastic.weather.WeatherUtils
import java.time.LocalTime

class HomeFragment : Fragment()  {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var title: TextView
    private lateinit var tempTextView: TextView
    private lateinit var rainTextView: TextView
    private lateinit var conditionTextView: TextView
    private lateinit var weatherLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        initWidgets()

        return root
    }

    private fun initWidgets() {
        title = binding.headerTextView
        tempTextView = binding.tempTextView
        rainTextView = binding.rainTextView
        conditionTextView = binding.conditionTextView
        weatherLayout = binding.weatherLayout
        try {
            setWeatherViewsOnWeatherUpdate()
        }catch(_:Exception){}
        setOtherViews()
    }

    private fun calculateTitle(hour: Int): String {
        if (hour>=20 || hour<5) return "Morning"
        else if(hour>17) return "Evening"
        else if(hour>12) return "Afternoon"
        else if(hour>11) return "Noon"
        else return "Morning"
    }

    @SuppressLint("SetTextI18n")
    private fun setOtherViews() {
        val hour = LocalTime.now().hour
        val timeTitle = calculateTitle(hour)

        title.text = "Good\n$timeTitle."
    }


    private fun setWeatherViewsOnWeatherUpdate(){
        Log.d("init","getting temperature")

        if(weather!=null) {
            setWeatherViews()
            return
        }
        try {
            Thread {
                var counter=0
                while (true) {
                    if (weather != null) {
                        setWeatherViews()
                        return@Thread
                    } else {
                        if (counter>=30)
                            throw Exception("Error: Timed out!")
                        Thread.sleep(100)
                        counter++
                    }
                }
            }.start()

        }catch (e:Exception){
            Log.d("Weather","Failed to get temperature.." + e.message)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherViews(){
        activity?.runOnUiThread {
            tempTextView.text = weather!!.currentTemp.toString() + "°C"
            rainTextView.text = weather!!.currentRainChance.toString() + "%"
            conditionTextView.text = weather!!.currentCondition
            val weatherBackgroundImage = WeatherUtils.getImages(weather!!.currentCondition,
                if(weather!!.currentHour >=20 || weather!!.currentHour<5) "night" else "day")
            weatherLayout.setBackgroundResource(weatherBackgroundImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}