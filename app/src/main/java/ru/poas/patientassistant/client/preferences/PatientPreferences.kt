package ru.poas.patientassistant.client.preferences

import android.content.Context
import android.content.SharedPreferences

object PatientPreferences {
    private lateinit var patientPreferences: SharedPreferences

    private const val PREFERENCES_NAME = "user_preferences"
    private const val ACTUAL_DRUG_NOTIFICATIONS_VERSION = "actual_notification_version"

    fun init(context: Context) {
        patientPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun updateActualDrugNotificationsVersion(version: Long) {
        val editor: SharedPreferences.Editor = patientPreferences.edit()
        with(editor) {
            putLong(ACTUAL_DRUG_NOTIFICATIONS_VERSION, version)
            apply()
        }
    }

    fun getActualDrugNotificationVersion() = patientPreferences.getLong(ACTUAL_DRUG_NOTIFICATIONS_VERSION, 0)
}