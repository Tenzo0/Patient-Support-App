package ru.poas.patientassistant.client.db.recommendations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommendations_database")
data class RecommendationEntity(
    @PrimaryKey
    val id: Long
)