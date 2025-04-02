package com.example.daytastic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.DrawableContainer.DrawableContainerState
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.daytastic.ui.calender.CalendarCellModel
import com.example.daytastic.ui.calender.CalendarEvent
import com.example.daytastic.ui.calender.CalendarEventsInstance
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CalendarAdapter(
    private val context: Context,
    private val daysOfMonth: ArrayList<CalendarCellModel>,
    private val onItemListener: OnItemListener
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val context = ContextThemeWrapper(parent.context,R.style.Theme_DayTastic_Dark)
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.15).toInt()
        return CalendarViewHolder(view, onItemListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.dayOfMonth.text = daysOfMonth[position].day
        //Log.d("Calendar cell","Position: "+position+" Day: "+daysOfMonth[position])
        //holder.cellLayout.setBackgroundResource(R.drawable.calendar_cell_border)
        if(daysOfMonth[position].day == ""){
            return
        }
        val date = LocalDate.parse(daysOfMonth[position].date!!.format(DateTimeFormatter.ISO_LOCAL_DATE))
        addEvents(holder,CalendarEventsInstance.getEventsListOfDate(date))
        if(daysOfMonth[position].date!!.atStartOfDay().isEqual(TodayDate.date.atStartOfDay())) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue,true)
            holder.cellLayout.setBackgroundColor(typedValue.data)
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue,true)
            holder.dayOfMonth.setTextColor(typedValue.data)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addEvents(holder: CalendarViewHolder, eventsListOfDate: MutableList<CalendarEvent>?) {
        if(eventsListOfDate.isNullOrEmpty()){
            return
        }
        val eventListLL = holder.eventListLL
        val typedValue = TypedValue()
        eventsListOfDate.sortBy { event -> event.startTime }
        eventsListOfDate.forEach{ event ->
            val eventItem = TextView(eventListLL.context).apply {
                context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondaryContainer, typedValue,true)
                setTextColor(typedValue.data)
                setBackgroundResource(R.drawable.calendar_cell_event_item)
                text = event.name
                setPadding(8, 2, 2, 2)
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    4F,
                    eventListLL.context.resources.displayMetrics
                )
                setOnClickListener { holder.onClick(eventListLL) }
                layoutParams = ViewGroup.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            val drawable = eventItem.background as LayerDrawable
            val innerBorder = drawable.findDrawableByLayerId(R.id.inner_border) as GradientDrawable
            innerBorder.setStroke(5, event.color)
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