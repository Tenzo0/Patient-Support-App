/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.vo

import com.squareup.moshi.Json
import ru.poas.patientassistant.client.patient.db.glossary.GlossaryEntity
import ru.poas.patientassistant.client.utils.parseServerContent

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
    parseServerContent(content)
)

fun List<GlossaryItem>.asDatabaseModel(): List<GlossaryEntity> = map { it.asDatabaseModel() }