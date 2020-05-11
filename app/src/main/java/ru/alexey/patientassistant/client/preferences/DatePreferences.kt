/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.preferences

import android.content.Context
import android.content.SharedPreferences

object DatePreferences {
    private lateinit var datePreferences: SharedPreferences
    private const val PREFERENCES_NAME = "date_preferences"

    private const val ACTUAL_DATE = "actual_server_date"

    fun init(context: Context) {
        datePreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun setActualServerDate(date: String?) {
        val editor: SharedPreferences.Editor = datePreferences.edit()
        with(editor) {
            putString(ACTUAL_DATE, date)
            apply()
        }
    }

    fun getActualServerDate() = datePreferences.getString(ACTUAL_DATE, null)
}