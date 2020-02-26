package ru.poas.patientassistant.client.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.api.RecommendationNetwork
import ru.poas.patientassistant.client.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.db.recommendations.asDomainModel
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.vo.UserRecommendation
import ru.poas.patientassistant.client.vo.asDatabaseModel

class RecommendationsRepository(private val database: RecommendationsDatabase) {
    val userRecommendation: LiveData<UserRecommendation> =
        Transformations.map(database.recommendationsDao.getUserRecommendation()) { it?.asDomainModel() }

    /**
     * refresh the glossary in the offline cache
     */
    suspend fun refreshRecommendations(credentials: String) {
        withContext(Dispatchers.IO) {
            val recommendation = RecommendationNetwork.recommendationService
                .getUserRecommendationsByPatientId(credentials, UserPreferences.getId())
                .body()

            database.recommendationsDao.clear()
            database.recommendationsDao.insert(recommendation!!.asDatabaseModel())
        }
    }
}