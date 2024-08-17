package com.example.daytastic

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.ViewFlipper
import androidx.fragment.app.FragmentActivity
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture


class DayEventsDialog(a: FragmentActivity) : Dialog(a) {

    init {
        this.setOwnerActivity(a)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_day)
        init()
    }

    private fun init(){
        val vp = findViewById<ViewFlipper>(R.id.calendar_view_flipper)
        val alarmTimeBeforeSpinner = findViewById<Spinner>(R.id.alarmTimeBeforeSpinner)
        val rgColorPicker = findViewById<RadioGroup>(R.id.color_picker_main)
        val startTimeButton = findViewById<Button>(R.id.start_time_button)
        val endTimeButton = findViewById<Button>(R.id.end_time_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        val savebutton = findViewById<Button>(R.id.save_button)
        val items = listOf("00:00","00:05","00:10","00:15","00:30","00:45","01:00")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alarmTimeBeforeSpinner.adapter = adapter

        cancelButton.setOnClickListener { this.cancel() }
        savebutton.setOnClickListener {saveEvent()}

        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        startTimeButton.text = time
        endTimeButton.text = time

        startTimeButton.setOnClickListener{view -> getTime(view)}
        endTimeButton.setOnClickListener{view -> getTime(view)}


        Log.d("DAY EVENT DIALOG","view flipper:${vp.displayedChild}")
        vp.showNext()
        Log.d("DAY EVENT DIALOG","view flipper:${vp.displayedChild}")

        createColorPalette(rgColorPicker)
    }
    private fun getTime(view: View){
        val newDialog = Dialog(context)
        newDialog.setContentView(R.layout.time_selector_layout)
        newDialog.show()
        val saveTimeButton = newDialog.findViewById<Button>(R.id.saveTimeButton)
        val cancelTimeButton = newDialog.findViewById<Button>(R.id.cancelTimeButton)
        val timePicker = newDialog.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)
        timePicker.hour = (view as Button).text.split(':')[0].toInt()
        timePicker.minute = view.text.split(':')[1].toInt()

        cancelTimeButton.setOnClickListener { newDialog.cancel() }
        saveTimeButton.setOnClickListener {
            val time = LocalTime.of(timePicker.hour,timePicker.minute)
            view.text = time.toString()
            newDialog.cancel()
        }
    }

    private fun createColorPalette(rgColorPicker:RadioGroup) {
        val colorPalette = ownerActivity!!.resources.getStringArray(R.array.color_palette)

        colorPalette.indices.forEach { i ->
            //create radio button by inflating radio button layout
            val inflater = LayoutInflater.from(ownerActivity)
            val rbView: View = inflater.inflate(R.layout.custom_radio_button, null)
            val rb = rbView.rootView as RadioButton

            rb.id = i


            rb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorPalette[i]))

            rgColorPicker.addView(rb)
        }
    }
    private fun saveEvent(){

    }
}