package ru.poas.patientassistant.client.patient.workers

import android.content.Context
import androidx.work.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import ru.poas.patientassistant.client.patient.repository.RecommendationsRepository
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateTimeFormat
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleTimeFormat
import ru.poas.patientassistant.client.utils.DateUtils.getDaysCountBetween
import timber.log.Timber
import kotlin.math.abs
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RecommendationsWorker private constructor(
    appContext: Context,
    params: WorkerParameters,
    private val recommendationsRepository: RecommendationsRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        try {
            UserPreferences.init(applicationContext)
            val operationDate = UserPreferences.getOperationDate()
            val currentDate = databaseSimpleDateFormat.format(Date())
            val recommendationDay = getDaysCountBetween(operationDate!!, currentDate)
            recommendationsRepository.deliverNotificationIfRecommendationExist(
                applicationContext, recommendationDay
            )
        } catch (e: NullPointerException) {
            Timber.e("recommendation worker NPE: UserPreferences.getOperationDate() = ${UserPreferences.getOperationDate()}")
            Result.failure()
        }
        return Result.success()
    }

    class Factory @Inject constructor(private val recommendationsRepository: RecommendationsRepository): ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return RecommendationsWorker(appContext, params, recommendationsRepository)
        }
    }
}

//TODO increase precision of trigger time
fun setupRecommendationWorker(applicationContext: Context) {
    //Current hour of day in millis
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    //Trigger hour in millis
    val triggerTime =
        if(currentHour < 8)
            8 - currentHour
        else
            24 - currentHour + 8

    Timber.i("recommendation worker work starts after $triggerTime hours} ")

    //Trigger time is 8:00-9:00
    val repeatingRequest = PeriodicWorkRequestBuilder<RecommendationsWorker>(
        1, TimeUnit.DAYS, triggerTime.toLong(), TimeUnit.HOURS)
        .build()

    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
        "B2Doc recommendations notifications",
        ExistingPeriodicWorkPolicy.REPLACE,
        repeatingRequest
    )
}