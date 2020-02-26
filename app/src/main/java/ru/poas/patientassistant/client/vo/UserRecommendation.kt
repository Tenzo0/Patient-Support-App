package ru.poas.patientassistant.client.vo

import com.squareup.moshi.Json
import ru.poas.patientassistant.client.db.recommendations.UserRecommendationEntity

data class UserRecommendation (
    @Json(name = "operationDate") val operationDate: String,
    @Json(name = "recommendationId") val recommendationId: Long
)

fun UserRecommendation.asDatabaseModel(): UserRecommendationEntity = UserRecommendationEntity(
    operationDate,
    recommendationId
)

fun List<UserRecommendation>.asDatabaseModel(): List<UserRecommendationEntity> = map { it.asDatabaseModel() }