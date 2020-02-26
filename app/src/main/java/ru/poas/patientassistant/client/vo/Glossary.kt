package ru.poas.patientassistant.client.vo

import ru.poas.patientassistant.client.db.glossary.GlossaryEntity

data class Glossary(
    val id: Long,
    val authorId: Long,
    val authorFirstName: String,
    val authorLastName: String,
    val content: String,
    val dateOfCreation: String,
    val link: String,
    val shortTitle: String,
    val title: String,
    val visible: Boolean
)

fun Glossary.asDatabaseModel(): GlossaryEntity = GlossaryEntity(
    id,
    authorId,
    authorFirstName,
    authorLastName,
    content,
    dateOfCreation,
    link.orEmpty(),
    shortTitle,
    title,
    visible
)

fun List<Glossary>.asDatabaseModel(): List<GlossaryEntity> = map { it.asDatabaseModel() }