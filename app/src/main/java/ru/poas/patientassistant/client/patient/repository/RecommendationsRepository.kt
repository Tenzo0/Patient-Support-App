/*
 * Copyright (c) Alexey Barykin 2020. 
 */

package ru.poas.patientassistant.client.patient.repository

import android.accounts.NetworkErrorException
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.R
import ru.poas.patientassistant.client.patient.api.RecommendationNetwork
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationConfirmKeyEntity
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.patient.db.recommendations.asDomainModel
import ru.poas.patientassistant.client.patient.ui.PatientActivity
import ru.poas.patientassistant.client.patient.vo.Recommendation
import ru.poas.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.poas.patientassistant.client.patient.vo.asDatabaseModel
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.utils.NOTIFICATION_CHANNEL
import timber.log.Timber
import javax.inject.Inject

class RecommendationsRepository @Inject constructor(
    private val database: RecommendationsDatabase
) {
    val recommendationsList: LiveData<List<Recommendation>> =
        Transformations.map(database.recommendationsDao.getAllRecommendations()) { it.asDomainModel() }

    private var _isRecommendationConfirmed = MutableLiveData<Boolean>()

    val isRecommendationConfirmed: LiveData<Boolean>
        get() = _isRecommendationConfirmed

    init {
        _isRecommendationConfirmed.value = true
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
        }
    }

    suspend fun refreshRecommendations(credentials: String, recommendationId: Long) {
        withContext(Dispatchers.IO) {
            val recommendations = RecommendationNetwork.recommendationService
                .getRecommendationListById(credentials, recommendationId)

            Timber.i("recommendations refresh with code ${recommendations.code()}")
            database.recommendationsDao.insert(recommendations.body()!!.asDatabaseModel())
            Timber.i("recommendations inserted into database: ${database.recommendationsDao.getAllRecommendations().value}")
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

    suspend fun deliverNotificationIfRecommendationExist(context: Context, day: Int) {
        withContext(Dispatchers.IO) {
            if(database.recommendationsDao.isRecommendationExist(day)) {
                Timber.i("trying to create and deliver recommendation notification")
                createAndDeliverNotification(context)
            }
            else Timber.i("no recommendations found in database for notification")
        }
    }

    private fun createAndDeliverNotification(context: Context) {
        val startDrugFragmentIntent = PendingIntent.getActivity(context, 0,
            Intent(context, PatientActivity::class.java).apply { putExtra("fragment", "Recommendations") },
            PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.mainPrimary))
            .setContentIntent(startDrugFragmentIntent)
            .setAutoCancel(true)
            .setGroup("RecommendationsNotifications")
            .setContentTitle(context.getString(R.string.today_recommendations))
            .setContentText(context.getString(R.string.open_today_recommendations))
            .build()

        //Deliver notification
        notification?.let {
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
        }
    }

    companion object {
        val notificationId = "recommendationNotification".hashCode()
    }
}