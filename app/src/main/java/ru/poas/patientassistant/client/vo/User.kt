package ru.poas.patientassistant.client.vo

import com.squareup.moshi.Json

data class User(
    @Json(name = "id") val id: Long,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "roles") val roles: List<Role>,
    @Json(name = "isTemporaryPassword") val isTemporaryPassword: Boolean

    //@Json(name = "password") val password: String,

)