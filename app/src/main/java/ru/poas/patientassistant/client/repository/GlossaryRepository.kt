package ru.poas.patientassistant.client.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.api.GlossaryNetwork
import ru.poas.patientassistant.client.db.glossary.GlossaryDatabase
import ru.poas.patientassistant.client.db.glossary.asDomainModel
import ru.poas.patientassistant.client.vo.Glossary
import ru.poas.patientassistant.client.vo.asDatabaseModel
import timber.log.Timber

class GlossaryRepository(private val database: GlossaryDatabase) {

    val glossary: LiveData<List<Glossary>> =
        Transformations.map(database.glossaryDao.getAll()) { it.asDomainModel() }

    /**
     * refresh the glossary in the cache
     */
    suspend fun refreshGlossary(token: String) {
        withContext(Dispatchers.IO) {
            Timber.d("refresh glossary")
            val glossary = GlossaryNetwork.glossaryService.getGlossary(token).body().orEmpty()
            database.glossaryDao.clear()
            database.glossaryDao.insert(glossary.asDatabaseModel())
        }
    }
}