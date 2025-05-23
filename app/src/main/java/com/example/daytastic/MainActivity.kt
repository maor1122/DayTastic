package com.example.daytastic

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daytastic.DateHandler.selectedDate
import com.example.daytastic.data.EventViewModel
import com.example.daytastic.databinding.ActivityCalendarBinding
import com.example.daytastic.ui.ThemeHelper
import com.example.daytastic.ui.calender.CalendarCellModel
import com.example.daytastic.ui.calender.CalendarEventsHandler
import com.example.daytastic.weather.Weather
import com.example.daytastic.weather.WeatherInstance
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import com.google.gson.Gson
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var btn_prev: MaterialButton
    private lateinit var btn_next: MaterialButton
    private lateinit var mEventViewModel: EventViewModel
    private lateinit var calendarAdapter: CalendarAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(getThemeFromPrefs())
        DynamicColors.applyToActivitiesIfAvailable(application)
        CalendarEventsHandler.initEventViewModel(this)
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater) // Inflate the correct binding
        setContentView(binding.root)
        initWeather()


        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
        btn_prev = binding.btnPrev
        btn_next = binding.btnNext

        btn_prev.setOnClickListener { view -> previousMonthAction() }
        btn_next.setOnClickListener { view -> nextMonthAction() }

        selectedDate = DateHandler.todayDate


        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<CalendarCellModel> = daysInMonthArray(selectedDate)
        calendarAdapter = CalendarAdapter(this,this,daysInMonth,this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
        mEventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        mEventViewModel.events.observe(this) { event ->
            calendarAdapter.setData(event)
        }
        mEventViewModel.updateEvents()
    }

    fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<CalendarCellModel> = daysInMonthArray(selectedDate)
        mEventViewModel.updateEvents()
        calendarAdapter.updateDays(daysInMonth)
    }

    @SuppressLint("DefaultLocale")
    private fun daysInMonthArray(date: LocalDate): ArrayList<CalendarCellModel> {
        val daysInMonthArray = ArrayList<CalendarCellModel>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        var dayOfWeek = firstOfMonth.dayOfWeek.value
        if(dayOfWeek==7)
            dayOfWeek=0
        val matchingDate = selectedDate == DateHandler.todayDate
        val dayOfTheMonth = DateHandler.todayDate.dayOfMonth
        var date:LocalDate? = null
        //Log.d("Calender","day of week: $dayOfWeek, days in month: $daysInMonth")
        //Log.d("Weather","weather.days.size: ${WeatherInstance.weather!!.days.size}")


        for (i in 1..42) {
            val day: String
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                day = ""
            } else {
                day = ((i - dayOfWeek).toString())
                date = LocalDate.parse("${selectedDate.year}-${String.format("%02d",selectedDate.monthValue)}-${String.format("%02d",day.toInt())}",
                    DateTimeFormatter.ISO_LOCAL_DATE)
            }
            if(matchingDate && (day==dayOfTheMonth.toString())){
                counter = 6
            }
            daysInMonthArray.add(CalendarCellModel(day,date))
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private fun previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
        counter = 0
        Log.d("Calender","todayDate: ${selectedDate==DateHandler.todayDate}, selectedDate: $selectedDate")
    }

    private fun nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
        Log.d("Calender","todayDate: ${selectedDate==DateHandler.todayDate}, selectedDate: $selectedDate")
    }

    private fun initWeather() {
        val pref: SharedPreferences = getSharedPreferences("weather",0)
        val weatherString = pref.getString("weather",null)
        if(weatherString!=null){
            val weather = Gson().fromJson(weatherString, Weather::class.java)
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

    private fun getThemeFromPrefs(): Int {
        return when (ThemeHelper.getSavedTheme(this)) {
//            "Theme.MyApp.Light" ->  R.style.Theme_DayTastic_Light
//            "Theme.MyApp.Green" -> R.style.Theme_DayTastic_Green
            else -> R.style.Theme_DayTastic_Dark
        }
    }
}
