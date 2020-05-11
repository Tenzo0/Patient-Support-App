/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.db.drugs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.alexey.patientassistant.client.patient.domain.DrugItem

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
    @ColumnInfo val manufacturer: String,
    @ColumnInfo val realDateTimeOfMedicationReception: String?
)

fun DrugEntity.asDomainObject(): DrugItem =
    DrugItem(
        id,
        dose,
        doseTypeName,
        dateOfMedicationReception,
        timeOfMedicationReception,
        drugUnitId,
        name,
        description,
        manufacturer,
        realDateTimeOfMedicationReception
    )

fun List<DrugEntity>.asDomainObject(): List<DrugItem> = map { it.asDomainObject() }