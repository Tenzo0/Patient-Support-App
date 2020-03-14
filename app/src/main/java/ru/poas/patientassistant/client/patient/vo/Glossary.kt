package ru.poas.patientassistant.client.patient.vo

import com.squareup.moshi.Json

data class Glossary (
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "recommendationId") val recommendationId: Long,
    @field:Json(name = "glossaryItems") val glossaryItems: List<GlossaryItem>
)