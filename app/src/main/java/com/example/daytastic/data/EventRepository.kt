package com.example.daytastic.data

import androidx.lifecycle.LiveData
import java.util.Date
import java.util.Dictionary

class EventRepository(private val eventDao: EventDao) {

    suspend fun insert(event:CalendarEvent){
        eventDao.insert(event)
    }

    suspend fun delete(event: CalendarEvent){
        eventDao.delete(event)
    }

    fun getEventsByMonth(yearMonth: String): LiveData<List<CalendarEvent>>{
        return eventDao.getEventsForMonth(yearMonth)
    }

    fun getEventsAroundMonth(yearMonth: String): LiveData<List<CalendarEvent>>{
        return eventDao.getEventsAroundMonth(yearMonth)
    }


}