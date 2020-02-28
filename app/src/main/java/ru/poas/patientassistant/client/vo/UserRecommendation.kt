package ru.poas.patientassistant.client.vo

import com.squareup.moshi.Json

data class UserRecommendation (
    @Json(name = "operationDate") val operationDate: String,
    @Json(name = "recommendationId") val recommendationId: Long
)