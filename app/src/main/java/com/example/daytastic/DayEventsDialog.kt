package com.example.daytastic

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity


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

    }
}