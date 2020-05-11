/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.vo

import com.squareup.moshi.Json

data class UserRecommendation (
    @Json(name = "operationDate") val operationDate: String,
    @Json(name = "recommendationId") val recommendationId: Long
)