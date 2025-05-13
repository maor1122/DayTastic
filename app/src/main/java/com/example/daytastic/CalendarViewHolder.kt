package com.example.daytastic

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class CalendarViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView){
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
    val cellLayout: ConstraintLayout = itemView.findViewById(R.id.constraint_c_cell)
    val eventListLL: LinearLayout = itemView.findViewById(R.id.calendarCellEventListLL)


//    init {
//        itemView.setOnClickListener(this)
//    }

//    override fun onClick(view: View) {
//        onItemListener.onItemClick(adapterPosition, dayOfMonth.text as String)
//    }
}