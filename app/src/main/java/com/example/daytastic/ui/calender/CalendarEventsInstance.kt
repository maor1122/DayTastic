package com.example.daytastic.ui.calender

import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.daytastic.reminders.scheduleNotification
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CalendarEventsInstance {
    private var events: MutableMap<String,MutableList<CalendarEvent>> = hashMapOf()

    fun deleteEvent(event:CalendarEvent){
        if(events.contains(event.date))
            events[event.date]?.remove(event)
    }

    fun deleteEventAndUpdate(event:CalendarEvent,context: Context){
        deleteEvent(event)
        Thread { saveEvents(context) }.start()
    }

    fun addEvent(event:CalendarEvent,context:Context){
        if(event.alarmType=="Notification"){
            addNotification(context,event)
        }
        else if(event.alarmType=="Alarm"){

        }
        if(events.contains(event.date))
            events[event.date]?.add(event)
        else{
            events[event.date] = mutableListOf(event)
        }
        Thread { saveEvents(context) }.start()
    }

    private fun addNotification(context:Context, event: CalendarEvent) {
        val time = getLocalTimeDate(event)
        val name = event.name
        scheduleNotification(context,time,name)
    }

    private fun getLocalTimeDate(event:CalendarEvent) : LocalDateTime{
        val date = LocalDate.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE)
        val time = LocalTime.parse(event.startTime)
        return LocalDateTime.of(date,time)
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