package com.example.daytastic.ui.calender

import android.os.Bundle
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
    private lateinit var btn_prev: MaterialButton
    private lateinit var btn_next: MaterialButton

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        selectedDate = LocalDate.now();
        setMonthView()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<String> = daysInMonthArray(selectedDate)

        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)

        val daysInMonth = yearMonth.lengthOfMonth()

        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
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
    }
    fun nextMonthAction(view: View) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }
}
