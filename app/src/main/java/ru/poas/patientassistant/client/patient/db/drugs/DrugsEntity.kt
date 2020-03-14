package ru.poas.patientassistant.client.patient.db.drugs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DrugsEntity(
    @PrimaryKey val id: Long
)