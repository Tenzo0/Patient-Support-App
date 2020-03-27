package ru.poas.patientassistant.client.patient.vo

import com.squareup.moshi.Json
import ru.poas.patientassistant.client.patient.db.drugs.DrugEntity

data class Medicament (
    @Json(name = "id") val id: Long,
    @Json(name = "medicament") val medicamentUnit: MedicamentUnit,
    @Json(name = "dose") val dose: Double,
    @Json(name = "doseTypeName") val doseTypeName: String,
    @Json(name = "dateOfMedicationReception") val dateOfMedicationReception: String,
    @Json(name = "timeOfMedicationReception") val timeOfMedicationReception: String
    //@Json(name = "realDateTimeOfMedicationReception") val realDateTimeOfMedicationReception
)
{
    data class MedicamentUnit (
        @Json(name = "id") val id: Long,
        @Json(name = "name") val name: String,
        @Json(name = "description") val description: String,
        @Json(name = "manufacturer") val manufacturer: String,
        @Json(name = "realDateTimeOfMedicationReception")
        val realDateTimeOfMedicationReception: String?
    )
}

fun Medicament.asDatabaseModel(): DrugEntity = DrugEntity(
    id,
    dose,
    doseTypeName,
    dateOfMedicationReception,
    timeOfMedicationReception,
    medicamentUnit.id,
    medicamentUnit.name,
    medicamentUnit.description,
    medicamentUnit.manufacturer,
    medicamentUnit.realDateTimeOfMedicationReception
)

fun List<Medicament>.asDatabaseModel(): List<DrugEntity> = map { it.asDatabaseModel() }