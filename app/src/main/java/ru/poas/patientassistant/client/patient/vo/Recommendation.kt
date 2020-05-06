/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.vo

import com.squareup.moshi.Json
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationEntity
import ru.poas.patientassistant.client.patient.domain.RecommendationNotificationItem
import ru.poas.patientassistant.client.utils.parseServerContent

data class Recommendation(
    @Json(name = "id") val id: Long,
    @Json(name = "recommendationId") val recommendationId: Long,
    @Json(name = "day") val day: Int,
    @Json(name = "recommendationUnit") val recommendationUnit: RecommendationUnit
)

data class RecommendationUnit(
    @Json(name = "id") val id: Long,
    @Json(name = "content") val content: String,
    @Json(name = "importantContent") val importantContent: String,
    @Json(name = "message") val message: String
)

fun Recommendation.asDatabaseModel(): RecommendationEntity =
    RecommendationEntity(
        id,
        recommendationId,
        day,
        recommendationUnit.id,
        parseServerContent(recommendationUnit.content),
        parseServerContent(recommendationUnit.importantContent),
        parseServerContent(recommendationUnit.message)
    )

fun Recommendation.asNotificationItem(version: Long): RecommendationNotificationItem =
    RecommendationNotificationItem(
        id,
        day,
        version
    )

fun List<Recommendation>.asDatabaseModel(): List<RecommendationEntity> = map { it.asDatabaseModel() }