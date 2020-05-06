/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.db.recommendations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.poas.patientassistant.client.patient.vo.Recommendation
import ru.poas.patientassistant.client.patient.vo.RecommendationUnit

@Entity(tableName = "recommendations_database")
data class RecommendationEntity(
    @ColumnInfo val id: Long,
    @ColumnInfo val recommendationId: Long,
    @ColumnInfo val day: Int,
    @PrimaryKey val recommendationUnitId: Long,
    @ColumnInfo val content: String,
    @ColumnInfo val importantContent: String,
    @ColumnInfo val message: String
)
fun RecommendationEntity.asDomainModel(): Recommendation = Recommendation(
    id,
    recommendationId,
    day,
    RecommendationUnit(
        recommendationUnitId,
        content,
        importantContent,
        message
    )
)

fun List<RecommendationEntity>.asDomainModel(): List<Recommendation>
        = map { it.asDomainModel() }

@Entity(tableName = "recommendations_confirm_keys_db")
data class RecommendationConfirmKeyEntity(
    @PrimaryKey val recommendationUnitId: Long,
    @ColumnInfo val isConfirmed: Boolean
)