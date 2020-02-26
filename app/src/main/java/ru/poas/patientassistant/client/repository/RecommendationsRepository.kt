package ru.poas.patientassistant.client.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.vo.Recommendation
import ru.poas.patientassistant.client.db.RecommendationsDatabase

class RecommendationsRepository(private val database: RecommendationsDatabase) {
    val allRecommendations: LiveData<List<Recommendation>> =
        Transformations.map(database.recommendationsDao.getAll()) {
            TODO("return it.asDomainModel()")
        }

    /**
     * refresh the glossary in the offline cacheЛ
     */
    suspend fun refreshGlossary(token: String) {
        withContext(Dispatchers.IO) {
            //TODO(clear and insert new values in database)
        }
    }
}