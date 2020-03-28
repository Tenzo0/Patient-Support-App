/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.receivers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.*
import ru.poas.patientassistant.client.B2DocApplication
import ru.poas.patientassistant.client.patient.repository.DrugsRepository
import ru.poas.patientassistant.client.patient.workers.setupDrugsWorker
import ru.poas.patientassistant.client.patient.workers.setupRecommendationWorker
import timber.log.Timber
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var drugsRepository: DrugsRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Timber.i("rebooted")
            (context.applicationContext as B2DocApplication).appComponent.inject(this)

            val refreshNotificationsScope = CoroutineScope(Dispatchers.IO + Job())
            refreshNotificationsScope.launch {
                drugsRepository.refreshNotificationsFromDatabase()
            }

            setupDrugsWorker(context.applicationContext)
            setupRecommendationWorker(context.applicationContext)
        }
    }

    companion object {
        fun enableBootReceiver(context: Context) {
            val receiverComponent = ComponentName(context, BootReceiver::class.java)
            val pm = context.packageManager

            pm.setComponentEnabledSetting(
                receiverComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}