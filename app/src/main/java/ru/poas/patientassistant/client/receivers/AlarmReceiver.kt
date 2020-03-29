/*
 * Copyright (c) Alexey Barykin 2020. 
 */

package ru.poas.patientassistant.client.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.joda.time.MutableDateTime
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.patient.domain.DrugNotificationItem
import ru.poas.patientassistant.client.patient.ui.PatientActivity
import ru.poas.patientassistant.client.preferences.PatientPreferences
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleTimeFormat
import ru.poas.patientassistant.client.utils.NOTIFICATION_CHANNEL
import timber.log.Timber
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val intentExtras = intent.extras

        //Get type of received intent information
        intentExtras?.getInt(ALARM_TYPE)?.let {
            when(it) {
                //If intent contain notification then create and deliver one
                NOTIFICATION_ALARM -> {
                    createAndDeliverNotification(context, intentExtras)
                }
            }
        }
    }

    private fun createAndDeliverNotification(context: Context, bundle: Bundle) {
        var notificationId = 0

        PatientPreferences.init(context)

        //Create notification
        val notification: Notification? =
            when(bundle.getInt(NOTIFICATION_TYPE)) {
                DRUG_NOTIFICATION -> {
                    val drugItem: DrugNotificationItem? = bundle
                        .getBundle(DRUG_NOTIFICATION_BUNDLE)?.getParcelable(DRUG_NOTIFICATION_ITEM)

                    val drugNotificationsActualVersion = PatientPreferences.getActualDrugNotificationVersion()

                    //check received drug item on null and
                    //check is drugItem contain actual notification info
                    val currentCalendar = Calendar.getInstance()
                    if (drugItem != null && drugItem.version == drugNotificationsActualVersion &&
                            drugItem.dateOfDrugReception == databaseSimpleDateFormat.format(Date())) //the same dates)
                    {
                        notificationId = "Drug".hashCode()

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
            val startDrugFragmentIntent = PendingIntent.getActivity(context, 0,
            Intent(context, PatientActivity::class.java).apply { putExtra("fragment", "Drugs") },
            PendingIntent.FLAG_UPDATE_CURRENT)
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.mainPrimary))
                .setContentIntent(startDrugFragmentIntent)
                .setAutoCancel(true)
                .setGroup("DrugsNotifications")
                .setContentTitle(context.getString(R.string.time_to_apply_drugs))
                .setContentText("${drugItem.name} ${drugItem.dose} ${drugItem.doseTypeName}")
                .build()
        }
        else null

    companion object {
        const val ALARM_TYPE = "T"
        const val NOTIFICATION_ALARM = 0

        const val NOTIFICATION_TYPE = "N"

        //Drug notifications
        const val DRUG_NOTIFICATION = 0
        const val DRUG_NOTIFICATION_BUNDLE = "DrugNotificationBundle"
        const val DRUG_NOTIFICATION_ITEM = "DrugNotificationItem"
    }
}
