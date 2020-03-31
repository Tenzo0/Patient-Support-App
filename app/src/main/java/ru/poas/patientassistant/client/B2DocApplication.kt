/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import ru.poas.patientassistant.client.di.AppComponent
import ru.poas.patientassistant.client.di.AppWorkerFactory
import ru.poas.patientassistant.client.di.DaggerAppComponent
import ru.poas.patientassistant.client.preferences.DatePreferences
import ru.poas.patientassistant.client.receivers.BootReceiver.Companion.enableBootReceiver
import ru.poas.patientassistant.client.utils.NOTIFICATION_CHANNEL
import ru.poas.patientassistant.client.utils.createChannel
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


open class B2DocApplication: Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: AppWorkerFactory

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent: AppComponent by lazy {
        initializeAppComponent()
    }

    private fun initializeAppComponent(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        appComponent.inject(this)

        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel(NOTIFICATION_CHANNEL)

        enableBootReceiver(this)

        DatePreferences.init(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
}