package com.example.daytastic.ui.calender

import java.time.LocalDate
import java.time.LocalTime

data class CalendarEvent(var name: String,var alarmType: String, var color: Int, var startTime: String,
                         var endTime:String,var date: String,var alarmTiming:String)
