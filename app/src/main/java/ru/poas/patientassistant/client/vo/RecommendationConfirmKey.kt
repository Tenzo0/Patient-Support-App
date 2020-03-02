package ru.poas.patientassistant.client.vo

import com.squareup.moshi.Json

data class RecommendationConfirmKey(
    @Json(name = "patientId") val patientId: Long,
    @Json(name = "recommendationId") val recommendationId: Long
)