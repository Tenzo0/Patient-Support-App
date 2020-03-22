package ru.poas.patientassistant.client.patient.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.poas.patientassistant.client.patient.api.DrugsNetwork
import ru.poas.patientassistant.client.patient.db.drugs.DrugsDatabase
import ru.poas.patientassistant.client.patient.db.drugs.asDomainObject
import ru.poas.patientassistant.client.patient.domain.DrugItem
import ru.poas.patientassistant.client.patient.vo.asDatabaseModel
import javax.inject.Inject

class DrugsRepository @Inject constructor(
    private val drugsDatabase: DrugsDatabase) {

    val drugsList: LiveData<List<DrugItem>> = map(drugsDatabase.drugsDao.getAll()) { it.asDomainObject() }

    suspend fun refreshDrugs(credentials: String) {
        withContext(Dispatchers.IO) {
            val drugs = DrugsNetwork.drugsService
                .getAllUnitsAssignedToPatient(credentials).body()
            drugsDatabase.drugsDao.clear()
            drugs?.asDatabaseModel()?.let { drugsDatabase.drugsDao.insert(it) }
        }
    }
}