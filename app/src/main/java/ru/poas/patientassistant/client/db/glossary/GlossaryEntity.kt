package ru.poas.patientassistant.client.db.glossary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.poas.patientassistant.client.vo.Glossary

@Entity(tableName = "glossary_database")
data class GlossaryEntity(

    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "author_id")
    val authorId: Long,

    @ColumnInfo(name = "author_first_name")
    val authorFirstName: String,

    @ColumnInfo(name = "author_last_name")
    val authorLastName: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "date_of_operation")
    val dateOfCreation: String,

    @ColumnInfo(name = "link")
    val link: String,

    @ColumnInfo(name = "short_title")
    val shortTitle: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "visible")
    val visible: Boolean
)

/**
 * Create glossary domain object from glossary database entity object
 * @return glossary domain object created from glossary database entity object
 */
fun GlossaryEntity.asDomainModel(): Glossary = Glossary(
    id,
    authorId,
    authorFirstName,
    authorLastName,
    content.replace("<NEXT_LINE>", "\n"),
    dateOfCreation,
    link,
    shortTitle,
    title,
    visible
)

fun List<GlossaryEntity>.asDomainModel(): List<Glossary> = map { it.asDomainModel() }