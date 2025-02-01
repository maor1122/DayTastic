package com.example.daytastic

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daytastic.ui.calender.CalendarCellModel
import com.example.daytastic.ui.calender.CalendarEvent
import com.example.daytastic.ui.calender.CalendarEventsInstance
import com.example.daytastic.weather.WeatherInstance.weather
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CalendarAdapter(
    private val daysOfMonth: ArrayList<CalendarCellModel>,
    private val onItemListener: OnItemListener
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.15).toInt()
        return CalendarViewHolder(view, onItemListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.dayOfMonth.text = daysOfMonth[position].day
        if(daysOfMonth[position].day == ""){
            holder.weatherLayout.visibility= LinearLayout.GONE
            return
        }
        holder.cellLayout.setBackgroundResource(R.drawable.calendar_cell_border)
        val date = LocalDate.parse(daysOfMonth[position].date!!.format(DateTimeFormatter.ISO_LOCAL_DATE))
        addEvents(holder.eventListLL,CalendarEventsInstance.getEventsListOfDate(date))
        if(daysOfMonth[position].date!!.atStartOfDay().isEqual(TodayDate.date.atStartOfDay())) {
            holder.cellLayout.setBackgroundResource(R.drawable.calendar_cell_border_today)
            holder.weatherLayout.visibility = LinearLayout.GONE
        }
        else {
            val diff = Duration.between(TodayDate.date.atStartOfDay(),daysOfMonth[position].date!!.atStartOfDay()).toDays()
            if (weather!= null && diff in 1..<weather!!.days.size){
                holder.tempTextView.text = weather!!.days[diff.toInt()].aTemp.toString() + "°C"
                holder.conditionTextView.text = weather!!.days[diff.toInt()].condition
            }
            else
                holder.weatherLayout.visibility= LinearLayout.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addEvents(eventListLL: LinearLayout, eventsListOfDate: MutableList<CalendarEvent>?) {
        if(eventsListOfDate.isNullOrEmpty()){
            return
        }
        eventsListOfDate.sortBy { event -> event.startTime }
        eventsListOfDate.forEach{ event ->

            val eventItem = TextView(eventListLL.context)
            eventItem.text = event.name
            eventItem.setBackgroundColor(event.color)
            eventListLL.addView(eventItem)
        }
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String?)
    }
}