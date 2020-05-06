/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.vo

import com.squareup.moshi.Json

data class Role(
    @Json(name = "description") val description: String,
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String
)