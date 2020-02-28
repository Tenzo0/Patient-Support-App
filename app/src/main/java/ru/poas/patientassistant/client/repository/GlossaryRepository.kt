package ru.poas.patientassistant.client.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.api.GlossaryNetwork
import ru.poas.patientassistant.client.api.RecommendationNetwork
import ru.poas.patientassistant.client.db.glossary.GlossaryDatabase
import ru.poas.patientassistant.client.db.glossary.asDomainModel
import ru.poas.patientassistant.client.preferences.UserPreferences
import ru.poas.patientassistant.client.vo.GlossaryItem
import ru.poas.patientassistant.client.vo.User
import ru.poas.patientassistant.client.vo.asDatabaseModel

class GlossaryRepository(private val database: GlossaryDatabase) {

    val glossaryItems: LiveData<List<GlossaryItem>> =
        Transformations.map(database.glossaryDao.getAll()) { it.asDomainModel() }

    /**
     * refresh the glossary in the cache
     */
    suspend fun refreshGlossary(credentials: String) {
        withContext(Dispatchers.IO) {
            val recommendation = RecommendationNetwork.recommendationService.getUserRecommendationsByPatientId(
                credentials, UserPreferences.getId()
            ).body()

            recommendation?.let {
                val glossary = GlossaryNetwork.glossaryService
                    .getGlossary(credentials, it.recommendationId)
                    .body()
                database.glossaryDao.clear()
                glossary?.glossaryItems?.asDatabaseModel()?.let {
                    database.glossaryDao.insert(it)
                }
            }
        }
    }
}