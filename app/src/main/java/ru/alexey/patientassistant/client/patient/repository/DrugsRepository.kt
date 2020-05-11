/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.repository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.alexey.patientassistant.client.patient.api.DrugsNetwork
import ru.alexey.patientassistant.client.patient.db.drugs.DrugsDatabase
import ru.alexey.patientassistant.client.patient.db.drugs.asDomainObject
import ru.alexey.patientassistant.client.patient.domain.DrugItem
import ru.alexey.patientassistant.client.patient.domain.asNotificationItem
import ru.alexey.patientassistant.client.patient.domain.drugsStartFromDate
import ru.alexey.patientassistant.client.patient.vo.asDatabaseModel
import ru.alexey.patientassistant.client.preferences.PatientPreferences
import ru.alexey.patientassistant.client.receivers.AlarmReceiver
import ru.alexey.patientassistant.client.utils.DateUtils
import ru.alexey.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.alexey.patientassistant.client.utils.DateUtils.databaseSimpleDateTimeFormat
import ru.alexey.patientassistant.client.utils.setExactAlarmAndAllowWhileIdle
import timber.log.Timber
import java.util.*
import javax.inject.Inject


/**
 * [DrugsRepository] has methods for working with Drugs network service, notifications service
 *  and database. When any refresh method is called it immediately updates all notifications
 *  which were upload from the server for chosen date range.
 */
class DrugsRepository @Inject constructor(
    private val context: Context,
    private val drugsDatabase: DrugsDatabase) {

    val drugsList: LiveData<List<DrugItem>> = map(drugsDatabase.drugsDao.getAllLiveData()) { listOfEntities ->
        listOfEntities.asDomainObject().sortedWith(compareBy
            { databaseSimpleDateTimeFormat.parse(it.dateOfDrugReception + 'T' + it.timeOfDrugReception) }
        )
    }

    suspend fun refreshNotificationsFromDatabase() {
        withContext(Dispatchers.IO) {
            val drugs = drugsDatabase.drugsDao.getAll().asDomainObject()
            updateNotifications(drugs)
        }
    }

    suspend fun refreshDrugs(credentials: String) {
        withContext(Dispatchers.IO) {
            val drugs = DrugsNetwork.drugsService
                .getAllUnitsAssignedToPatient(credentials).body()

            drugs?.let {
                drugsDatabase.drugsDao.clear()

                val drugsEntitiesList = drugs.asDatabaseModel()
                drugsDatabase.drugsDao.insert(drugsEntitiesList)

                updateNotifications(drugsEntitiesList.asDomainObject())
            }
        }
    }

    suspend fun refreshDrugsByDate(credentials: String, date: String) {
        withContext(Dispatchers.IO) {
            val drugs = DrugsNetwork.drugsService
                .getAllUnitsAssignedToPatientAnyDate(credentials, date).body()
            drugsDatabase.drugsDao.deleteByDate(date)

            val drugsEntitiesList = drugs!!.asDatabaseModel()
            drugsDatabase.drugsDao.insert(drugsEntitiesList)

            updateNotifications(drugsEntitiesList.asDomainObject())
        }
    }

    suspend fun refreshDrugsByDateRange(credentials: String, firstDate: String, lastDate: String) {
        withContext(Dispatchers.IO) {
            //Get updates from server
            val drugs = DrugsNetwork.drugsService
                .getAllUnitsAssignedToPatientByDateRange(credentials, firstDate, lastDate).body()

            //Update values in database
            var dateForDelete = firstDate
            val daysCount = DateUtils.getDaysCountBetween(firstDate, lastDate)
            for (i in 0..daysCount) {
                //delete entities associated with this date
                drugsDatabase.drugsDao.deleteByDate(dateForDelete)

                //inc date by one day in each iteration
                dateForDelete = DateUtils.getDatePlusDays(dateForDelete, 1)
            }

            val drugsEntitiesList = drugs!!.asDatabaseModel()
            drugsDatabase.drugsDao.insert(drugsEntitiesList)

            updateNotifications(drugsEntitiesList.asDomainObject())
        }
    }

    suspend fun confirmDrug(credentials: String, id: Long) {
        withContext(Dispatchers.IO) {
            DrugsNetwork.drugsService.confirmMedicamentUnit(credentials, id)
            drugsDatabase.drugsDao.confirmDrugById(id, databaseSimpleDateFormat.format(Date()))
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    @Synchronized
    suspend fun updateNotifications(drugsList: List<DrugItem>?) {
        withContext(Dispatchers.IO) {
            Timber.i("updating drug notifications")
            if (!drugsList.isNullOrEmpty()) {
                //setup notifications only for drugs that begin in current date
                val drugsStartFromCurrentDate = drugsList.drugsStartFromDate(Date())

                //update actual info about notifications version
                PatientPreferences.init(context)
                val currentVersion = PatientPreferences.getActualDrugNotificationVersion() + 1
                PatientPreferences.updateActualDrugNotificationsVersion(currentVersion)
                Timber.i("current stable version of drug notifications is $currentVersion, " +
                        "actual drugs list size is ${drugsStartFromCurrentDate.size}")

                for (drug in drugsStartFromCurrentDate) {
                    //Get trigger time for notification from drug item
                    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    val triggerTime = DateUtils.databaseSimpleTimeFormat.parse(drug.timeOfDrugReception).time +
                            DateUtils.databaseSimpleDateFormat.parse(drug.dateOfDrugReception).time

                    //Create intent that contains notification type and current drug item
                    val notificationPendingIntent: PendingIntent = PendingIntent
                        .getBroadcast(
                            context,
                            drug.id.toInt(),
                            Intent(context, AlarmReceiver::class.java).apply {
                                putExtra(AlarmReceiver.ALARM_TYPE, AlarmReceiver.NOTIFICATION_ALARM)
                                putExtra(
                                    AlarmReceiver.NOTIFICATION_TYPE,
                                    AlarmReceiver.DRUG_NOTIFICATION
                                )
                                putExtra(AlarmReceiver.DRUG_NOTIFICATION_BUNDLE, Bundle().apply {
                                    putParcelable(
                                        AlarmReceiver.DRUG_NOTIFICATION_ITEM,
                                        drug.asNotificationItem(currentVersion)
                                    )
                                })
                            },
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                    //Set exact alarm for current drug
                    setExactAlarmAndAllowWhileIdle(
                        context,
                        triggerTime,
                        notificationPendingIntent
                    )
                }
            }
        }
    }
}