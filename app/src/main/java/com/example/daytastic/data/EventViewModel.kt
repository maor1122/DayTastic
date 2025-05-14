package com.example.daytastic.data

import androidx.lifecycle.LiveData
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.daytastic.DateHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.YearMonth

class EventViewModel(application: Application): AndroidViewModel(application) {
    val loadedMonths = mutableSetOf<String>()
    val _events = MutableLiveData<Map<String, List<CalendarEvent>>>()
    val events: LiveData<Map<String, List<CalendarEvent>>> = _events

    private val repository: EventRepository

    init{
        val eventDao = EventDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        updateEvents()
    }

    fun updateEvents(yearMonth: String = DateHandler.selectedDate.toString().dropLast(3)) {
        val monthsToLoad = (-2..2).map {
            YearMonth.parse(yearMonth).plusMonths(it.toLong()).toString()
        }.filter { it !in loadedMonths }

        Log.d("EventViewModel:updatedEvents","updatedEvents called, adding ${monthsToLoad.size} months around $yearMonth")
        if (monthsToLoad.isEmpty()) return

                viewModelScope.launch {
            monthsToLoad.forEach { ym ->
                launch {
                    repository.getEventsByMonth(ym).asFlow().collect { list ->
                        val grouped = list.groupBy { it.date }
                        val current = _events.value?.toMutableMap() ?: mutableMapOf()
                        current.putAll(grouped)
                        _events.value = current // Use postValue from background thread
                        loadedMonths.add(ym)
                    }
                }
            }
            Log.d("EventViewModel:updatedEvents", "finished launching collectors for months")
            Log.d("EventViewModel:updatedEvents", "_events: ${_events.value?.keys}, events: ${events.value?.keys}")
        }
    }

    fun addEvent(event:CalendarEvent){
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(event)
        }
        updateEvents()
    }

    fun deleteEvent(event:CalendarEvent){
        // Remove the event from the database
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(event)
        }

        // Remove it from the _events variable
        val current = _events.value?.toMutableMap() ?: return
        val updatedDayEvents = current[event.date]?.toMutableList()?.apply {
            removeIf { it.id == event.id }
        }

        if (updatedDayEvents != null) {
            if (updatedDayEvents.isEmpty()) {
                current.remove(event.date)
            } else {
                current[event.date] = updatedDayEvents
            }
        }
        _events.value = current
        updateEvents()
    }


}