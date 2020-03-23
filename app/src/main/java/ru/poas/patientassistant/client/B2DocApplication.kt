package ru.poas.patientassistant.client

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import ru.poas.patientassistant.client.di.AppComponent
import ru.poas.patientassistant.client.di.DaggerAppComponent
import ru.poas.patientassistant.client.utils.NOTIFICATION_CHANNEL
import ru.poas.patientassistant.client.utils.createChannel
import timber.log.Timber
import timber.log.Timber.DebugTree


open class B2DocApplication: Application() {

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel(NOTIFICATION_CHANNEL)
    }
}