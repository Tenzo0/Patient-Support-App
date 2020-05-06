/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.db.glossary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.poas.patientassistant.client.patient.vo.GlossaryItem

@Entity(tableName = "glossary_database")
data class GlossaryEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo val shortTitle: String,
    @ColumnInfo val title: String,
    @ColumnInfo val content: String
)

/**
 * Create glossary domain object from glossary database entity object
 * @return glossary domain object created from glossary database entity object
 */
fun GlossaryEntity.asDomainModel(): GlossaryItem = GlossaryItem(
    content,
    id,
    shortTitle,
    title
)

fun List<GlossaryEntity>.asDomainModel(): List<GlossaryItem> = map { it.asDomainModel() }