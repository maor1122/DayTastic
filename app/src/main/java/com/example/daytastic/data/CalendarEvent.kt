package com.example.daytastic.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "events", indices = [Index(value = ["date"])])
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val name: String, val alarmType: String,
    val color: Int, val startTime: String,
    val endTime:String,
    val date: String,
    val alarmTiming: String
)
