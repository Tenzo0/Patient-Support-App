package ru.poas.patientassistant.client.patient.repository

import android.accounts.NetworkErrorException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.patient.api.RecommendationNetwork
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationConfirmKeyEntity
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.patient.db.recommendations.asDomainModel
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.patient.vo.Recommendation
import ru.poas.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.poas.patientassistant.client.patient.vo.asDatabaseModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecommendationsRepository @Inject constructor(
    private val database: RecommendationsDatabase
) {
    val recommendationsList: LiveData<List<Recommendation>> =
        Transformations.map(database.recommendationsDao.getAllRecommendations()) { it.asDomainModel() }

    private var _isRecommendationConfirmed = MutableLiveData<Boolean>()

    val isRecommendationConfirmed: LiveData<Boolean>
        get() = _isRecommendationConfirmed

    private var _operationDate = MutableLiveData<Calendar>()
    val operationDate: LiveData<Calendar>
        get() = _operationDate

    private var databaseDateFormat: SimpleDateFormat

    init {
        _isRecommendationConfirmed.value = true
        _operationDate.value = Calendar.getInstance()
        databaseDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ru", "RU"))
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
                _operationDate.value?.time = databaseDateFormat.parse(it.operationDate)!!
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
}