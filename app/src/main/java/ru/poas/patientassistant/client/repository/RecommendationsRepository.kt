package ru.poas.patientassistant.client.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.api.RecommendationNetwork
import ru.poas.patientassistant.client.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.db.recommendations.asDomainModel
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.vo.Recommendation
import ru.poas.patientassistant.client.vo.asDatabaseModel
import timber.log.Timber

class RecommendationsRepository(private val database: RecommendationsDatabase) {
    val recommendationsList: LiveData<List<Recommendation>> =
        Transformations.map(database.recommendationsDao.getUserRecommendation()) { it.asDomainModel() }

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

            Timber.i(recommendations.code().toString())
            database.recommendationsDao.clear()
            database.recommendationsDao.insert(recommendations.body()!!.asDatabaseModel())
        }
    }
}