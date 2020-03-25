package ru.poas.patientassistant.client.receivers

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.patient.domain.DrugItem
import ru.poas.patientassistant.client.patient.domain.DrugNotificationItem
import ru.poas.patientassistant.client.preferences.PatientPreferences
import ru.poas.patientassistant.client.utils.NOTIFICATION_CHANNEL
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {

    private var drugNotificationsActualVersion: Long = PatientPreferences.getActualNotificationVersion()

    override fun onReceive(context: Context, intent: Intent) {
        val intentExtras = intent.extras

        //Get type of received intent information
        intentExtras?.getString("type")?.let {
            when(it) {
                //If intent contain notification then create and deliver one
                "notification" -> createAndDeliverNotification(context, intentExtras)
            }
        }
    }

    private fun createAndDeliverNotification(context: Context, bundle: Bundle) {
        var notificationId = 0

        //Create notification
        val notification: Notification? =
            when(bundle.getString("notificationType")) {
                "drugNotification" -> {
                    val drugItem: DrugNotificationItem? = bundle
                        .getBundle("DrugNotificationItemBundle")?.getParcelable("DrugNotificationItem")

                    //check received drug item on null and
                    //check is drugItem contain actual notification info
                    if (drugItem != null && drugItem.version >= drugNotificationsActualVersion) {
                        notificationId = drugItem.id.toInt()
                        drugNotificationsActualVersion = drugItem.version // set current version as actual
                        //create Drug notification
                        createDrugNotification(context, drugItem)
                    }
                    else
                        null
                }
                else -> null
            }

        //Deliver created notification if it's exist
        notification?.let {
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun createDrugNotification(context: Context, drugItem: DrugNotificationItem?): Notification? =
        if (drugItem != null) {
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.time_to_apply_drugs))
                .setContentText("${drugItem.name} ${drugItem.dose} ${drugItem.doseTypeName}")
                .build()
        }
        else null
}
