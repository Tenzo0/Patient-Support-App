package ru.poas.patientassistant.client.vo

import com.squareup.moshi.Json
import ru.poas.patientassistant.client.db.glossary.GlossaryEntity

data class GlossaryItem(
    @field:Json(name = "content") val content: String,
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "shortTitle") val shortTitle: String,
    @field:Json(name = "title") val title: String
)

fun GlossaryItem.asDatabaseModel(): GlossaryEntity = GlossaryEntity(
    id,
    shortTitle,
    title,
    content
)

fun List<GlossaryItem>.asDatabaseModel(): List<GlossaryEntity> = map { it.asDatabaseModel() }