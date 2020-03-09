package ru.poas.patientassistant.client.db.drugs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DrugsEntity(
    @PrimaryKey val id: Long
)