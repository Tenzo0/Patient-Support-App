/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ru.alexey.patientassistant.client.R
import ru.alexey.patientassistant.client.patient.domain.DrugNotificationItem
import ru.alexey.patientassistant.client.patient.domain.RecommendationNotificationItem
import ru.alexey.patientassistant.client.patient.ui.PatientActivity
import ru.alexey.patientassistant.client.preferences.PatientPreferences
import ru.alexey.patientassistant.client.preferences.PatientPreferences.getLastDeliveredRecommendationNotificationDate
import ru.alexey.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.alexey.patientassistant.client.utils.DateUtils.isDateInRangeOfCurrent
import ru.alexey.patientassistant.client.utils.NOTIFICATION_CHANNEL
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
                    if (drugItem != null && drugItem.version == drugNotificationsActualVersion &&
                        isDateInRangeOfCurrent(30, drugItem.dateOfDrugReception + 'T' + drugItem.timeOfDrugReception))
                    {
                        notificationId = "Drug".hashCode()

                        //create Drug notification
                        createDrugNotification(context, drugItem)
                    }
                    else null
                }
                RECOMMENDATION_NOTIFICATION -> {
                    val recItem: RecommendationNotificationItem? = bundle
                        .getBundle(RECOMMENDATION_NOTIFICATION_BUNDLE)?.getParcelable(
                            RECOMMENDATION_NOTIFICATION_ITEM)

                    val currentDate = databaseSimpleDateFormat.format(Date())
                    val recommendationNotificationsActualVersion = PatientPreferences.getActualRecommendationNotificationVersion()
                    val lastDeliveredRecommendationNotificationDate = getLastDeliveredRecommendationNotificationDate()
                    //check received drug item on null and
                    //check is recItem contain actual notification info
                    if (recItem != null && recItem.version == recommendationNotificationsActualVersion &&
                        lastDeliveredRecommendationNotificationDate != currentDate)
                    {
                        PatientPreferences.updateLastDeliveredRecommendationNotificationDate(currentDate)
                        notificationId = "Recommendation".hashCode()

                        //create Recommendation notification
                        createRecommendationNotification(context, recItem)
                    }
                    else null
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
            defaultNotificationBuilder(context,startDrugFragmentIntent)
                .setGroup("DrugsNotifications")
                .setContentTitle(context.getString(R.string.time_to_apply_drugs))
                .setContentText("${drugItem.name} ${drugItem.dose} ${drugItem.doseTypeName}")
                .build()
        }
        else null

    private fun createRecommendationNotification(context: Context, recItem: RecommendationNotificationItem?): Notification? =
        if (recItem != null) {
            val startRecommendationFragmentIntent = PendingIntent.getActivity(context, 0,
                Intent(context, PatientActivity::class.java).apply { putExtra("fragment", "Recommendations") },
                PendingIntent.FLAG_UPDATE_CURRENT)
            defaultNotificationBuilder(context, startRecommendationFragmentIntent)
                .setAutoCancel(true)
                .setGroup("RecommendationsNotifications")
                .setContentTitle(context.getString(R.string.today_recommendations))
                .setContentText(context.getString(R.string.open_today_recommendations))
                .build()
        }
        else null

    private fun defaultNotificationBuilder(context: Context, contentIntent: PendingIntent) =
        NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.mainPrimary))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

    companion object {
        const val ALARM_TYPE = "T"
        const val NOTIFICATION_ALARM = 0

        const val NOTIFICATION_TYPE = "N"

        //Drug notifications
        const val DRUG_NOTIFICATION = 0
        const val DRUG_NOTIFICATION_BUNDLE = "DrugNotificationBundle"
        const val DRUG_NOTIFICATION_ITEM = "DrugNotificationItem"
        //Recommendation notifications
        const val RECOMMENDATION_NOTIFICATION = 1
        const val RECOMMENDATION_NOTIFICATION_BUNDLE = "DrugNotificationBundle"
        const val RECOMMENDATION_NOTIFICATION_ITEM = "DrugNotificationItem"
    }
}
