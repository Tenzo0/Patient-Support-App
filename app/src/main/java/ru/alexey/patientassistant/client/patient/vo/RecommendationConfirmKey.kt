/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.vo

import com.squareup.moshi.Json

data class RecommendationConfirmKey(
    @Json(name = "patientId") val patientId: Long,
    @Json(name = "recommendationId") val recommendationId: Long
)