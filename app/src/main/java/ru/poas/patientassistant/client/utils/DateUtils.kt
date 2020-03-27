package ru.poas.patientassistant.client.utils

import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

object DateUtils{
    private const val DATABASE_DATE_PATTERN = "yyyy-MM-dd"
    private const val DATABASE_TIME_PATTERN = "HH:mm:ss"
    val databaseSimpleDateFormat = SimpleDateFormat(DATABASE_DATE_PATTERN, Locale("ru", "RU"))
    val databaseSimpleTimeFormat = SimpleDateFormat(DATABASE_TIME_PATTERN, Locale("ru", "RU"))
        .apply { timeZone = TimeZone.getTimeZone("GMT") }
    val databaseDateFormatter: DateTimeFormatter by lazy { DateTimeFormat.forPattern(DATABASE_DATE_PATTERN) }
    val databaseTimeFormatter: DateTimeFormatter by lazy { DateTimeFormat.forPattern(DATABASE_TIME_PATTERN) }

    fun getDaysCountBetween(firstDate: String, lastDate: String): Int {
        val firstDateInDatabaseFormat = LocalDate
            .parse(firstDate, databaseDateFormatter)
        val lastDateInDatabaseFormat = LocalDate
            .parse(lastDate, databaseDateFormatter)
        return Days.daysBetween(firstDateInDatabaseFormat, lastDateInDatabaseFormat).days
    }

    fun getDatePlusDays(date: String, days: Int): String {
        val resultDate = DateTime.parse(date, databaseDateFormatter)
            .toMutableDateTime().apply{ addDays(days) }
        return databaseSimpleDateFormat.format(resultDate.toDate())
    }
}