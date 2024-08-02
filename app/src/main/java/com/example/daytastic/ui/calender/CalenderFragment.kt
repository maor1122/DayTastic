package com.example.daytastic.ui.calender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daytastic.CalendarAdapter
import com.example.daytastic.databinding.FragmentCalenderBinding
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class CalenderFragment : Fragment(), CalendarAdapter.OnItemListener {

    private var _binding: FragmentCalenderBinding? = null
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var selectedDate: LocalDate
    private lateinit var todayDate: LocalDate
    private lateinit var btn_prev: MaterialButton
    private lateinit var btn_next: MaterialButton

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var counter = 0

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val calenderViewModel =
            ViewModelProvider(this)[CalenderViewModel::class.java]

        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
        btn_prev = binding.btnPrev
        btn_next = binding.btnNext

        btn_prev.setOnClickListener { view -> previousMonthAction(view) }
        btn_next.setOnClickListener { view -> nextMonthAction(view) }

        selectedDate = LocalDate.now()
        todayDate = selectedDate
        setMonthView()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<CalenderCellModel> = daysInMonthArray(selectedDate)

        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<CalenderCellModel> {
        val daysInMonthArray = ArrayList<CalenderCellModel>()
        val yearMonth = YearMonth.from(date)

        val daysInMonth = yearMonth.lengthOfMonth()

        val firstOfMonth = selectedDate.withDayOfMonth(1)
        var dayOfWeek = firstOfMonth.dayOfWeek.value
        if(dayOfWeek==7)
            dayOfWeek=0
        val matchingDate = selectedDate == todayDate
        val afterWeek = todayDate.plusDays(7)
        val dayOfTheMonth = todayDate.dayOfMonth
        Log.d("Calender","day of week: $dayOfWeek, days in month: $daysInMonth")

        for (i in 1..42) {
            val day: String
            var today = false
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                day = ""
            } else {
                day = ((i - dayOfWeek).toString())
            }
            if(matchingDate && (day==dayOfTheMonth.toString())){
                counter = 6
                today=true
            }
            
            val daysFromToday = 7-counter--
            daysInMonthArray.add(CalenderCellModel(day,today,daysFromToday))
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    override fun onItemClick(position: Int, dayText: String?) {
        if (dayText != "") {
            val message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate)
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun previousMonthAction(view: View) {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
        counter = 0
        Log.d("Calender","selectedDate==todayDate: ${selectedDate==todayDate}")

    }
    fun nextMonthAction(view: View) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
        Log.d("Calender","selectedDate==todayDate: ${selectedDate==todayDate}")
    }
}
