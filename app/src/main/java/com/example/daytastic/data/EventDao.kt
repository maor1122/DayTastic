package com.example.daytastic.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.YearMonth

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: CalendarEvent)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventByID(id: Int): CalendarEvent

    @Query("SELECT * FROM events WHERE date = :date")
    suspend fun getEventByDate(date: String): List<CalendarEvent>


    @Query("SELECT * FROM events WHERE date LIKE :yearMonth || '%'")
    fun getEventsForMonth(yearMonth: String): LiveData<List<CalendarEvent>>

    @Delete
    suspend fun delete(event: CalendarEvent): Int

    @Query("SELECT * FROM events WHERE date BETWEEN :start AND :end")
    fun getEventsInRange(start: String,end: String): LiveData<List<CalendarEvent>>

    fun getRangeAroundMonth(yearMonth: String): Pair<String, String>{
        val month = YearMonth.parse(yearMonth)
        val start = month.minusMonths(1).atDay(1)
        val end = month.plusMonths(1).atEndOfMonth()
        return Pair(start.toString(), end.toString())  // format: yyyy-MM-dd
    }

    fun getEventsAroundMonth(yearMonth: String): LiveData<List<CalendarEvent>>{
        val (start, end) = getRangeAroundMonth(yearMonth)
        return getEventsInRange(start,end)
    }
}