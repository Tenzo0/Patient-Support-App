/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.vo

import com.squareup.moshi.Json

data class UserRecommendation (
    @Json(name = "operationDate") val operationDate: String,
    @Json(name = "recommendationId") val recommendationId: Long
)