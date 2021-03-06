/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build

const val NOTIFICATION_CHANNEL = "Notification channel"

/**
 * Creates a Notification channel, for OREO and higher.
 */
fun NotificationManager.createChannel(channelName: String) {

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelName,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            setShowBadge(false)
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = channelName
        }

        createNotificationChannel(notificationChannel)
    }
}

fun setExactAlarmAndAllowWhileIdle(context: Context, triggerTime: Long, pendingIntent: PendingIntent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //Ignore Doze mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime, pendingIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime, pendingIntent
        )
    }
}

fun setAlarm(context: Context, triggerTime: Long, pendingIntent: PendingIntent) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.set(
        AlarmManager.RTC_WAKEUP,
        triggerTime, pendingIntent
    )
}

