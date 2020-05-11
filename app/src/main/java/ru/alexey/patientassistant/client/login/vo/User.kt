/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.login.vo

import com.squareup.moshi.Json
import ru.alexey.patientassistant.client.patient.vo.Role

data class User(
    @Json(name = "id") val id: Long,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "roles") val roles: List<Role>,
    @Json(name = "isTemporaryPassword") val isTemporaryPassword: Boolean

    //@Json(name = "password") val password: String,

)