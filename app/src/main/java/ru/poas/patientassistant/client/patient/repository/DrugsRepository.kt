package ru.poas.patientassistant.client.patient.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.patient.api.DrugsNetwork
import ru.poas.patientassistant.client.patient.api.GlossaryNetwork
import ru.poas.patientassistant.client.patient.db.drugs.DrugsDatabase
import ru.poas.patientassistant.client.patient.db.drugs.DrugEntity
import ru.poas.patientassistant.client.patient.vo.asDatabaseModel
import ru.poas.patientassistant.client.preferences.UserPreferences
import javax.inject.Inject

class DrugsRepository @Inject constructor(
    private val drugsDatabase: DrugsDatabase) {

    val drugsList: LiveData<List<DrugEntity>> = drugsDatabase.drugsDao.getAll()

    suspend fun refreshDrugs(credentials: String) {
        withContext(Dispatchers.IO) {
            val drugs = DrugsNetwork.drugsService
                .getAllUnitsAssignedToPatient(credentials).body()

            drugsDatabase.drugsDao.clear()
            drugs?.asDatabaseModel()?.let { drugsDatabase.drugsDao.insert(it) }
        }
    }
}