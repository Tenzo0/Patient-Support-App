package ru.poas.patientassistant.client.patient.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import okhttp3.Credentials
import org.joda.time.LocalDate.fromDateFields
import retrofit2.HttpException
import ru.poas.patientassistant.client.patient.repository.DrugsRepository
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.utils.DateUtils
import ru.poas.patientassistant.client.utils.DateUtils.databaseSimpleDateFormat
import timber.log.Timber
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class DrugsWorker private constructor(
    appContext: Context,
    params: WorkerParameters,
    private val drugsRepository: DrugsRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        UserPreferences.init(applicationContext)

        Timber.i("try to run drugs repository updating")
        try {
            val currentDate = databaseSimpleDateFormat.format(Date())
            with(drugsRepository) {
                refreshDrugsByDateRange(
                    Credentials.basic(
                        UserPreferences.getPhone()!!,
                        UserPreferences.getPassword()!!
                    ),
                    currentDate,
                    DateUtils.getDatePlusDays(currentDate, 2)
                )
            }
            
        } catch (e: HttpException) {
            Timber.e("$e")
            return Result.failure()
        } catch (e: KotlinNullPointerException) {
            Timber.e("NullPointerException at DrugsWorker: phone=${UserPreferences.getPhone()}")
            return Result.failure()
        } catch (e: Exception) {
            Timber.e("unexpected worker exception! $e")
            return Result.failure()
        }
        Timber.i("all work has been done")
        return Result.success()
    }

    class Factory @Inject constructor(
        private val drugsRepository: DrugsRepository
    ): ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return DrugsWorker(appContext, params, drugsRepository)
        }
    }
}