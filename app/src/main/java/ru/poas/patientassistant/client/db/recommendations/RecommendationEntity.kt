package ru.poas.patientassistant.client.db.recommendations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.poas.patientassistant.client.vo.Recommendation
import ru.poas.patientassistant.client.vo.RecommendationUnit

@Entity(tableName = "recommendations_database")
data class RecommendationEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo val recommendationId: Long,
    @ColumnInfo val day: Int,
    @ColumnInfo val recommendationUnitId: Long,
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