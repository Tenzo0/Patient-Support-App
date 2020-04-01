/*
 * Copyright (c) Alexey Barykin 2020. 
 */

package ru.poas.patientassistant.client.patient.repository

import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.patient.api.RecommendationNetwork
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationConfirmKeyEntity
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.patient.db.recommendations.asDomainModel
import ru.poas.patientassistant.client.patient.vo.Recommendation
import ru.poas.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.poas.patientassistant.client.patient.vo.asDatabaseModel
import ru.poas.patientassistant.client.patient.vo.asNotificationItem
import ru.poas.patientassistant.client.preferences.PatientPreferences
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.receivers.AlarmReceiver
import ru.poas.patientassistant.client.utils.DateUtils
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleTimeFormat
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleTimeFormatWithoutTimeZone
import ru.poas.patientassistant.client.utils.setAlarm
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RecommendationsRepository @Inject constructor(
    private val context: Context,
    private val database: RecommendationsDatabase
) {
    val recommendationsList: LiveData<List<Recommendation>> =
        Transformations.map(database.recommendationsDao.getAllRecommendations()) { it.asDomainModel() }

    private var _isRecommendationConfirmed = MutableLiveData<Boolean>()

    val isRecommendationConfirmed: LiveData<Boolean>
        get() = _isRecommendationConfirmed

    init {
        _isRecommendationConfirmed.postValue(true)
    }

    /**
     * refresh the recommendation id in the offline cache
     */
    suspend fun refreshRecommendationInfo(credentials: String) {
        withContext(Dispatchers.IO) {
            val userRecommendation= RecommendationNetwork.recommendationService
                .getUserRecommendationsByPatientId(credentials, UserPreferences.getId())
                .body()
            userRecommendation?.let {
                UserPreferences.saveUserRecommendation(it)
            }

            refreshRecommendations(credentials, userRecommendation!!.recommendationId)
        }
    }

    private suspend fun refreshRecommendations(credentials: String, recommendationId: Long) {
        withContext(Dispatchers.IO) {
            val recommendations = RecommendationNetwork.recommendationService
                .getRecommendationListById(credentials, recommendationId)

            Timber.i("recommendations refresh with code ${recommendations.code()}")
            database.recommendationsDao.insert(recommendations.body()!!.asDatabaseModel())
            Timber.i("recommendations inserted into database: ${database.recommendationsDao.getAllRecommendations().value}")
            updateNotifications(recommendations.body())
        }
    }

    suspend fun confirmRecommendation(credentials: String, recommendationConfirmKey: RecommendationConfirmKey, recommendationUnitId: Long) {
        withContext(Dispatchers.IO) {
            if (RecommendationNetwork.recommendationService
                .putConfirmRecommendationKey(credentials, recommendationConfirmKey).code() != 200)
                throw NetworkErrorException("Confirm recommendation network error")

            database.recommendationsDao.insertConfirmation(
                RecommendationConfirmKeyEntity(
                    recommendationUnitId,
                    true
                )
            )

            _isRecommendationConfirmed.postValue(true)

            Timber.i("confirmRecommendation repo: key(${recommendationConfirmKey}), db: ${database.recommendationsDao
                .getIsRecommendationConfirmedById(recommendationUnitId)}, $recommendationUnitId")
        }
    }

    suspend fun getIsRecommendationConfirmedById(recommendationUnitId: Long) {
        withContext(Dispatchers.IO) {
            _isRecommendationConfirmed.postValue(database.recommendationsDao
                .getIsRecommendationConfirmedById(recommendationUnitId))
            Timber.i("getIsRecommendationConfirmedById repo ${isRecommendationConfirmed.value}")
            Timber.i("getIsRecommendationConfirmedById repo ${database.recommendationsDao
                .getIsRecommendationConfirmedById(recommendationUnitId)} with recUnitId $recommendationUnitId")
        }
    }

    suspend fun refreshNotificationsFromDatabase() {
        withContext(Dispatchers.IO) {
            val recommendationList = database.recommendationsDao.getAll().asDomainModel()
            updateNotifications(recommendationList)
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    @Synchronized
    suspend fun updateNotifications(recommendationList: List<Recommendation>?) {
        withContext(Dispatchers.IO) {
            Timber.i("updating drug notifications")
            if (!recommendationList.isNullOrEmpty()) {
                //update actual info about notifications version
                PatientPreferences.init(context)
                val currentVersion = PatientPreferences.getActualRecommendationNotificationVersion() + 1
                PatientPreferences.updateActualRecommendationsNotificationsVersion(currentVersion)

                UserPreferences.init(context)
                val operationDate = UserPreferences.getOperationDate()
                val currentDate = databaseSimpleDateFormat.format(Date())

                val daysFromOperationDate = DateUtils.getDaysCountBetween(operationDate!!, currentDate)
                val currentHour = databaseSimpleTimeFormatWithoutTimeZone.format(Date()).take(2)
                val triggerDay = if (currentHour.toInt() < 8)
                    //If user open app in night time then setup one more notification for morning
                    daysFromOperationDate - 1
                    //else start day for notification starts from next day
                else daysFromOperationDate

                val recommendationListStartsFromCurrentDate = recommendationList.filter {
                    it.day > triggerDay
                }

                Timber.i("updating ${recommendationListStartsFromCurrentDate.size} recommendations notifications")
                for (recommendation in recommendationListStartsFromCurrentDate) {

                    if(database.recommendationsDao.isRecommendationExist(daysFromOperationDate)) {
                        //Get trigger time for notification from drug item
                        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        val triggerTime = databaseSimpleDateFormat
                            .parse(DateUtils.getDatePlusDays(operationDate, recommendation.day)).time +
                                databaseSimpleTimeFormat.parse("08:00:00")!!.time
                        //Create intent that contains notification type and current drug item
                        val notificationPendingIntent: PendingIntent = PendingIntent
                            .getBroadcast(
                                context,
                                recommendation.id.toInt(),
                                Intent(context, AlarmReceiver::class.java).apply {
                                    putExtra(
                                        AlarmReceiver.ALARM_TYPE,
                                        AlarmReceiver.NOTIFICATION_ALARM
                                    )
                                    putExtra(
                                        AlarmReceiver.NOTIFICATION_TYPE,
                                        AlarmReceiver.RECOMMENDATION_NOTIFICATION
                                    )
                                    putExtra(
                                        AlarmReceiver.RECOMMENDATION_NOTIFICATION_BUNDLE,
                                        Bundle().apply {
                                            putParcelable(
                                                AlarmReceiver.RECOMMENDATION_NOTIFICATION_ITEM,
                                                recommendation.asNotificationItem(currentVersion)
                                            )
                                        })
                                },
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )

                        //Set exact alarm for current drug
                        setAlarm(
                            context,
                            triggerTime,
                            notificationPendingIntent
                        )
                    }
                }
            }
        }
    }
}