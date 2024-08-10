package com.example.daytastic

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.daytastic.ui.calender.CalenderCellModel
import com.example.daytastic.weather.WeatherInstance.weather
import java.time.Duration


class CalendarAdapter(
    private val daysOfMonth: ArrayList<CalenderCellModel>,
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
        if(daysOfMonth[position].date!!.atStartOfDay().isEqual(TodayDate.date.atStartOfDay())) {
            holder.cellLayout.setBackgroundResource(R.color.teal_200)
            holder.weatherLayout.visibility = LinearLayout.GONE
        }
        else {
            val diff = Duration.between(TodayDate.date.atStartOfDay(),daysOfMonth[position].date!!.atStartOfDay()).toDays()
            if (diff in 1..<weather!!.days.size){
                holder.tempTextView.text = weather!!.days[diff.toInt()].aTemp.toString() + "°C"
                holder.conditionTextView.text = weather!!.days[diff.toInt()].condition
            }
            else
                holder.weatherLayout.visibility= LinearLayout.GONE

        }
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String?)
    }
}