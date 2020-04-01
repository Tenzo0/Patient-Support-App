/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.preferences

import android.content.Context
import android.content.SharedPreferences

object PatientPreferences {
    private lateinit var patientPreferences: SharedPreferences

    private const val PREFERENCES_NAME = "user_preferences"
    private const val ACTUAL_DRUG_NOTIFICATIONS_VERSION = "actual_drugs_notification_version"
    private const val ACTUAL_RECOMMENDATION_NOTIFICATIONS_VERSION = "actual_recommendations_notification_version"
    private const val LAST_RECOMMENDATION_NOTIFICATION_DELIVER_DATE = "last_rec_notification"
    private const val LAST_NOTIFIED_RECOMMENDATION_DATE = "lnrd"

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

    fun updateActualRecommendationsNotificationsVersion(version: Long) {
        val editor: SharedPreferences.Editor = patientPreferences.edit()
        with(editor) {
            putLong(ACTUAL_RECOMMENDATION_NOTIFICATIONS_VERSION, version)
            apply()
        }
    }

    fun updateLastDeliveredRecommendationNotificationDate(date: String) {
        val editor: SharedPreferences.Editor = patientPreferences.edit()
        with(editor) {
            putString(LAST_RECOMMENDATION_NOTIFICATION_DELIVER_DATE, date)
            apply()
        }
    }

    fun getActualDrugNotificationVersion() = patientPreferences.getLong(ACTUAL_DRUG_NOTIFICATIONS_VERSION, 0)

    fun getActualRecommendationNotificationVersion() = patientPreferences.getLong(ACTUAL_RECOMMENDATION_NOTIFICATIONS_VERSION, 0)

    fun getLastDeliveredRecommendationNotificationDate() = patientPreferences
        .getString(LAST_RECOMMENDATION_NOTIFICATION_DELIVER_DATE, null)
}