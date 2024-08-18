package com.example.daytastic

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.fragment.app.FragmentActivity
import com.example.daytastic.ui.calender.CalendarEvent
import com.example.daytastic.ui.calender.CalendarEventsInstance
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.Throws


class DayEventsDialog(a: FragmentActivity, private val selectedDate: LocalDate) : Dialog(a) {
    private lateinit var eventNameED:EditText
    private lateinit var rgColorPicker:RadioGroup
    private lateinit var alarmTimeBeforeSpinner: Spinner
    private lateinit var alarmTypeRG: RadioGroup
    private lateinit var startTimeButton: Button
    private lateinit var endTimeButton: Button
    private lateinit var viewFlipper:ViewFlipper

    init {
        this.setOwnerActivity(a)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_day)
        init()
    }

    private fun init(){
        setWidgets()
        viewFlipper = findViewById(R.id.calendar_view_flipper)
        val clickedDateEvents = CalendarEventsInstance.getEventsListOfDate(selectedDate)
        if(clickedDateEvents.isNullOrEmpty()) {
            initNewEventDialog()
            viewFlipper.showNext()
        }
        else{
            initEventListDialog(clickedDateEvents)
        }
    }

    private fun initNewEventDialog(){
        findViewById<RadioButton>(R.id.notificationRB).isChecked = true
        alarmTypeRG.setOnCheckedChangeListener{ group,id->
            val buttonText = findViewById<RadioButton>(id).text
            if(buttonText =="None"){
                alarmTimeBeforeSpinner.isEnabled = false
            }
            else if (buttonText=="Notification" || buttonText=="Alarm"){
                alarmTimeBeforeSpinner.isEnabled = true
            }
        }
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        val saveButton = findViewById<Button>(R.id.save_button)
        val items = listOf("00:00","00:05","00:10","00:15","00:30","00:45","01:00")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alarmTimeBeforeSpinner.adapter = adapter

        cancelButton.setOnClickListener { this.cancel() }
        saveButton.setOnClickListener {saveEventButtonClicked()}

        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        startTimeButton.text = time
        endTimeButton.text = time
        startTimeButton.setOnClickListener{view ->
            getTime(view)
        }
        endTimeButton.setOnClickListener{view ->
            getTime(view)
        }

        createColorPalette(rgColorPicker)
    }

    @SuppressLint("SetTextI18n")
    private fun initEventListDialog(clickedDateEvents: MutableList<CalendarEvent>) {
        Log.d("addEventsToDialog","Selected Events Date: $selectedDate")
        val newEventImageView = findViewById<ImageView>(R.id.newEventButton)

        newEventImageView.setOnClickListener{
            initNewEventDialog()
            viewFlipper.showNext()
        }
        val eventsListLinearLayout = findViewById<LinearLayout>(R.id.events_linear_layout)
        clickedDateEvents.sortBy { event -> event.startTime }
        clickedDateEvents.forEach{ event ->
            val inflater = LayoutInflater.from(ownerActivity)
            val eventItem: View = inflater.inflate(R.layout.event_list_item, null)
            eventItem.findViewById<TextView>(R.id.eventNameTV).text = event.name
            eventItem.findViewById<TextView>(R.id.alarmTypeTV).text = event.alarmType
            eventItem.findViewById<TextView>(R.id.durationTV).text = "${event.startTime} - ${event.endTime}"
            (eventItem.findViewById<ImageView>(R.id.circleView).background as GradientDrawable).setColor(event.color)
            eventsListLinearLayout.addView(eventItem)
        }
    }

    private fun setWidgets() {
        eventNameED = findViewById(R.id.event_name_et)
        rgColorPicker = findViewById(R.id.color_picker_main)
        alarmTypeRG = findViewById(R.id.alarmTypeRG)
        alarmTimeBeforeSpinner = findViewById(R.id.alarmTimeBeforeSpinner)
        startTimeButton = findViewById(R.id.start_time_button)
        endTimeButton = findViewById(R.id.end_time_button)
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

    @SuppressLint("InflateParams")
    private fun createColorPalette(rgColorPicker:RadioGroup) {
        val colorPalette = ownerActivity!!.resources.getStringArray(R.array.color_palette)

        colorPalette.indices.forEach { i ->
            //create radio button by inflating radio button layout
            val inflater = LayoutInflater.from(ownerActivity)
            val rbView: View = inflater.inflate(R.layout.custom_radio_button, null)
            val rb = rbView.rootView as RadioButton

            rb.id = i


            rb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorPalette[i]))
            if(rb.id==0)
                rb.isChecked=true
            rgColorPicker.addView(rb)
        }
    }
    private fun saveEventButtonClicked(){
        val event = createEvent()
        Log.d("Save","${event.name},${event.alarmType},${event.color},${event.startTime},${event.endTime},${event.date},${event.alarmTiming}")
        try {
            saveEvent(event)
            this.cancel()
        }catch (e:Exception){
            Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
        }
    }
    private fun createEvent(): CalendarEvent{
        val alarmButtonIndex = alarmTypeRG.indexOfChild(findViewById(alarmTypeRG.checkedRadioButtonId))
        val alarmSelected = (alarmTypeRG.getChildAt(alarmButtonIndex) as RadioButton).text
        val colorButtonIndex = rgColorPicker.indexOfChild(findViewById(rgColorPicker.checkedRadioButtonId))
        val colorSelected = (rgColorPicker.getChildAt(colorButtonIndex) as RadioButton).backgroundTintList!!.defaultColor
        val startTime = LocalTime.parse(startTimeButton.text)
        val endTime = LocalTime.parse(endTimeButton.text)
        val alarmTimingString = alarmTimeBeforeSpinner.selectedItem.toString()
        return CalendarEvent(eventNameED.text.toString(),alarmSelected.toString(),colorSelected,startTime.toString(),endTime.toString(),
            selectedDate.toString(),alarmTimingString)
    }
    @Throws
    private fun saveEvent(event: CalendarEvent){
        if(event.name.isEmpty())
            throw Exception("Please fill event name")
        if(LocalTime.parse(event.startTime).isAfter(LocalTime.parse(event.endTime)))
            throw Exception("End time can't be after start time.")
        CalendarEventsInstance.addEvent(event,context)
    }
}