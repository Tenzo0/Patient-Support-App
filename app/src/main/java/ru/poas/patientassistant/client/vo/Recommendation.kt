package ru.poas.patientassistant.client.vo

import com.squareup.moshi.Json
import ru.poas.patientassistant.client.db.recommendations.RecommendationEntity

data class Recommendation(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "recommendationId") val recommendationId: Long,
    @field:Json(name = "day") val day: Int,
    @field:Json(name = "recommendationUnit") val recommendationUnit: RecommendationUnit
)

data class RecommendationUnit(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "importantContent") val importantContent: String,
    @field:Json(name = "message") val message: String
)

fun Recommendation.asDatabaseModel(): RecommendationEntity = RecommendationEntity(
    id,
    recommendationId,
    day,
    recommendationUnit.id,
    recommendationUnit.content,
    recommendationUnit.importantContent,
    recommendationUnit.message
)

fun List<Recommendation>.asDatabaseModel(): List<RecommendationEntity> = map { it.asDatabaseModel() }