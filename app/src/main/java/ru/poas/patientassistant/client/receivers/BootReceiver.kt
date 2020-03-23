package ru.poas.patientassistant.client.receivers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            //TODO set notifications
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