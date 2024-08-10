package com.example.daytastic

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.daytastic.CalendarAdapter.OnItemListener
import java.time.LocalDate


class CalendarViewHolder(itemView: View, private val onItemListener: OnItemListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
    val cellLayout: ConstraintLayout = itemView.findViewById(R.id.constraint_c_cell)
    val weatherLayout: LinearLayout = itemView.findViewById(R.id.weather_layout)
    val conditionTextView: TextView = itemView.findViewById(R.id.conditionTextView)
    val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, dayOfMonth.text as String)
    }
}