package com.example.daytastic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.daytastic.ui.calender.CalendarCellModel
import com.example.daytastic.data.CalendarEvent
import com.example.daytastic.ui.calender.CalendarEventsHandler
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CalendarAdapter(
    private val context: Context,
    private val activity: FragmentActivity,
    private var daysOfMonth: ArrayList<CalendarCellModel>,
    private val mainActivity: MainActivity
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
        var events = emptyMap<String,List<CalendarEvent>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val context = ContextThemeWrapper(parent.context,R.style.Theme_DayTastic_Dark)
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.15).toInt()
        return CalendarViewHolder(view)
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
        holder.itemView.setOnClickListener{
            if (daysOfMonth[position].day.isNotEmpty()) {
                DayEventsDialog(activity,date,events[date.toString()],mainActivity).show()
            }
        }
        val today = daysOfMonth[position].date!!.atStartOfDay().isEqual(DateHandler.todayDate.atStartOfDay())
        addEvents(holder,CalendarEventsHandler.getEventsListOfDate(date,events),today)
        if(today) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue,true)
            holder.cellLayout.setBackgroundColor(typedValue.data)
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue,true)
            holder.dayOfMonth.setTextColor(typedValue.data)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addEvents(holder: CalendarViewHolder, eventsListOfDate: List<CalendarEvent>?, today: Boolean) {
        if(eventsListOfDate.isNullOrEmpty()){
            return
        }
        val eventListLL = holder.eventListLL
        val typedValue = TypedValue()
        eventsListOfDate.sortedBy { event -> event.startTime }
        eventsListOfDate.forEach{ event ->
            val eventItem = TextView(eventListLL.context).apply {
                if(today){
                    context.theme.resolveAttribute(
                        com.google.android.material.R.attr.colorOnSurfaceInverse,
                        typedValue,
                        true
                    )
                    setBackgroundResource(R.drawable.calendar_cell_event_item_selected)
                }else {
                    context.theme.resolveAttribute(
                        com.google.android.material.R.attr.colorOnSecondaryContainer,
                        typedValue,
                        true
                    )
                    setBackgroundResource(R.drawable.calendar_cell_event_item)
                }
                setTextColor(typedValue.data)
                text = event.name
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTypeface(typeface,Typeface.BOLD)
                setPadding(8, 2, 2, 2)
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    4F,
                    eventListLL.context.resources.displayMetrics
                )
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

    fun setData(events: Map<String,List<CalendarEvent>>){
        this.events = events
        notifyDataSetChanged()
        Log.d("CalendarAdapter:setData","Data changed, events amount: ${events.size}")
    }

    fun updateDays(newDays: ArrayList<CalendarCellModel>) {
        this.daysOfMonth = newDays
        notifyDataSetChanged()
    }
}