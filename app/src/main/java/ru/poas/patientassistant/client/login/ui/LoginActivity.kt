/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.login.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.preferences.UserPreferences
import timber.log.Timber


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserPreferences.init(applicationContext)
        setContentView(R.layout.activity_login)
    }
}
