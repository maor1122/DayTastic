package com.example.daytastic.ui.calender

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.daytastic.data.CalendarEvent
import com.example.daytastic.data.EventViewModel
import com.example.daytastic.reminders.scheduleNotification
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CalendarEventsHandler {
    private lateinit var mEventViewModel: EventViewModel

    fun initEventViewModel(owner: ViewModelStoreOwner){
        mEventViewModel = ViewModelProvider(owner).get(EventViewModel::class.java)
    }

    fun deleteEvent(event: CalendarEvent){
        mEventViewModel.deleteEvent(event)
    }

    fun deleteEventAndUpdate(event: CalendarEvent, context: Context){
        deleteEvent(event)
    }

    fun addEvent(event: CalendarEvent, context:Context){
        if(event.alarmType=="Notification"){
            addNotification(context,event)
        }
        else if(event.alarmType=="Alarm"){
            addAlarm(context,event)
        }
        mEventViewModel.addEvent(event)
    }

    private fun addAlarm(context: Context, event: CalendarEvent) {
        // TODO: Implement addAlarm function
    }

    private fun addNotification(context:Context, event: CalendarEvent) {
        val time = getLocalTimeDate(event)
        val name = event.name
        scheduleNotification(context,time,name)
    }

    private fun getLocalTimeDate(event: CalendarEvent) : LocalDateTime{
        val date = LocalDate.parse(event.date, DateTimeFormatter.ISO_LOCAL_DATE)
        val time = LocalTime.parse(event.startTime)
        return LocalDateTime.of(date,time)
    }

    fun getEventsListOfDate(date:LocalDate, events:Map<String,List<CalendarEvent>>?): List<CalendarEvent>?{
        if (events == null)
            return emptyList()
        return events[date.toString()]
    }

}