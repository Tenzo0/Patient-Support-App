package ru.poas.patientassistant.client.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommendations_database")
data class RecommendationEntity(
    @PrimaryKey
    val id: Long
)