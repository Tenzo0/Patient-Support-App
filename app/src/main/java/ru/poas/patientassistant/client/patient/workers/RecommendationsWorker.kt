package ru.poas.patientassistant.client.patient.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import ru.poas.patientassistant.client.patient.repository.RecommendationsRepository
import ru.poas.patientassistant.client.preferences.PatientPreferences
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.poas.patientassistant.client.utils.DateUtils.getDaysCountBetween
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecommendationsWorker private constructor(
    appContext: Context,
    params: WorkerParameters,
    private val recommendationsRepository: RecommendationsRepository
) : CoroutineWorker(appContext, params) {

    @SuppressLint("BinaryOperationInTimber")
    override suspend fun doWork(): Result {
        Timber.i("recommendation worker work start now")
        PatientPreferences.init(applicationContext)
        val lastRecommendationsNotificationDate = PatientPreferences.getLastDeliveredRecommendationNotificationDate()
        val currentDate = databaseSimpleDateFormat.format(Date())

        //Try to deliver notification if it is not delivered today yet
        if (lastRecommendationsNotificationDate == null || lastRecommendationsNotificationDate != currentDate) {
            try {
                UserPreferences.init(applicationContext)
                val operationDate = UserPreferences.getOperationDate()
                val recommendationDay = getDaysCountBetween(operationDate!!, currentDate)
                Timber.i("recommendation day = $recommendationDay")
                recommendationsRepository.deliverNotificationIfRecommendationExist(
                    applicationContext, recommendationDay
                )
                PatientPreferences.updateLastDeliveredRecommendationNotificationDate(currentDate)
            } catch (e: NullPointerException) {
                Timber.e("recommendation worker NPE: (user isn't authorized?) " +
                        "UserPreferences.getOperationDate() = ${UserPreferences.getOperationDate()}")
                return Result.failure()
            }
        }
        return Result.success()
    }

    class Factory @Inject constructor(private val recommendationsRepository: RecommendationsRepository): ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return RecommendationsWorker(appContext, params, recommendationsRepository)
        }
    }
}

@SuppressLint("BinaryOperationInTimber")
fun setupRecommendationWorker(applicationContext: Context) {
    //Current hour of day in minutes
    val calendar = Calendar.getInstance()
    val currentMinuteOfDay = with(calendar) {
        get(Calendar.HOUR_OF_DAY) * 60 + get(Calendar.MINUTE)
    }

    //Default trigger time in minutes (8:00 am as minutes)
    val defaultTriggerTime = 8 * 60

    //Trigger hour in minutes (time elapsed to the nearest 8:00 am)
    val triggerTime =
        if(currentMinuteOfDay <= defaultTriggerTime)
            defaultTriggerTime - currentMinuteOfDay
        else
            24 * 60 - currentMinuteOfDay + defaultTriggerTime

    Timber.i("recommendation worker work starts after $triggerTime minute(s) " +
            "(now ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)})")

    //Trigger time is after 8:00-8:15
    val repeatingRequest = PeriodicWorkRequestBuilder<RecommendationsWorker>(
        1, TimeUnit.MINUTES, triggerTime.toLong(), TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
        "B2Doc recommendations notifications",
        ExistingPeriodicWorkPolicy.REPLACE,
        repeatingRequest
    )
}