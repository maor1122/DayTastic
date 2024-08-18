package com.example.daytastic.ui.calender

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CalendarEventsInstance {
    private var events: MutableMap<String,MutableList<CalendarEvent>> = hashMapOf()

    fun addEvent(event:CalendarEvent,context:Context){
        if(events.contains(event.date))
            events[event.date]?.add(event)
        else{
            events[event.date] = mutableListOf(event)
        }
        Thread { saveEvents(context) }.start()
    }

    private fun saveEvents(context: Context){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Daytastic", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(events)
        Log.d("saveEvents","json: $json")
        editor.putString("calendarEvents", json)
        editor.apply()
    }

    fun getEvents(context:Context){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Daytastic", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("calendarEvents", null)
        val type = object : com.google.gson.reflect.TypeToken<Map<String, List<CalendarEvent>>>() {}.type
        if (!json.isNullOrEmpty()) {
            try {
                events = gson.fromJson(json, type)
            }catch (e:Exception){
                Log.e("getEventsLocally","failed getting events, exception: ${e.message}")
                Log.d("getEventsLocally","json: $json")
            }
        }
    }

    fun getEventsListOfDate(date:LocalDate): MutableList<CalendarEvent>?{
        return events[date.toString()]
    }

}