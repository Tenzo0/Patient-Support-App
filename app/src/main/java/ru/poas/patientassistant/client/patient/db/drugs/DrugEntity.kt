package ru.poas.patientassistant.client.patient.db.drugs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.poas.patientassistant.client.patient.repository.DrugItem
import ru.poas.patientassistant.client.patient.vo.Medicament

@Entity(tableName = "drugs_database")
data class DrugEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo val dose: Double,
    @ColumnInfo val doseTypeName: String,
    @ColumnInfo val dateOfMedicationReception: String,
    @ColumnInfo val timeOfMedicationReception: String,
    @ColumnInfo val drugUnitId: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val description: String,
    @ColumnInfo val manufacturer: String
)

fun DrugEntity.asDomainObject(): DrugItem =
    DrugItem(
        id,
        dose,
        doseTypeName,
        dateOfMedicationReception,
        timeOfMedicationReception,
        name,
        description,
        manufacturer
    )

fun List<DrugEntity>.asDomainObject(): List<DrugItem> = map { it.asDomainObject() }