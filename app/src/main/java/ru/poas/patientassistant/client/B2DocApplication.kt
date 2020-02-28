package ru.poas.patientassistant.client

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class B2DocApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}