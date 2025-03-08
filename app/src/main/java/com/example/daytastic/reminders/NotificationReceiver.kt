package com.example.daytastic.reminders

import android.content.BroadcastReceiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.daytastic.R
import java.time.LocalDateTime
import java.time.ZoneId

fun scheduleNotification(context: Context, dateTime: LocalDateTime, message: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("message", message)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        dateTime.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerTime = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
    try{
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }catch (e:SecurityException){
            Toast.makeText(context, "Creating Notification for event: $message failed.\nException: ${e.message}", Toast.LENGTH_LONG).show()
        }
}

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "Notification"
        showNotification(context, message)
    }

    private fun showNotification(context: Context, message: String) {
        val channelId = "scheduled_notifications"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Scheduled Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_calendar_month_24)
            .setContentTitle("Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}