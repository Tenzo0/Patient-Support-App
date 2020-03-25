package ru.poas.patientassistant.client.preferences

import android.content.Context
import android.content.SharedPreferences

object PatientPreferences {
    private lateinit var patientPreferences: SharedPreferences

    private const val PREFERENCES_NAME = "user_preferences"
    private const val ACTUAL_NOTIFICATION_VERSION = "actual_notification_version"

    fun init(context: Context) {
        patientPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun updateActualNotificationVersion(version: Long) {
        val editor: SharedPreferences.Editor = patientPreferences.edit()
        with(editor) {
            putLong(ACTUAL_NOTIFICATION_VERSION, version)
            apply()
        }
    }

    fun getActualNotificationVersion() = patientPreferences.getLong(ACTUAL_NOTIFICATION_VERSION, 0)
}