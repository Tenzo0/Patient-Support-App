package ru.poas.patientassistant.client.utils

import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils{
    val DATABASE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale("ru", "RU"))
    val DATABASE_TIME_FORMAT = SimpleDateFormat("HH:mm:ss", Locale("ru", "RU")).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    fun getCountDaysBetween(firstDate: String, lastDate: String): Long {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val millisBetween = DATABASE_DATE_FORMAT.parse(firstDate).time -
                DATABASE_DATE_FORMAT.parse(lastDate).time
        return TimeUnit.DAYS.convert(millisBetween, TimeUnit.MILLISECONDS)
    }

    fun getIncDate(date: String): String {
        val parsedDate = DATABASE_DATE_FORMAT.parse(date)
        val dateTime = LocalDate.fromDateFields(parsedDate).apply { plusDays(1) }
        return DATABASE_DATE_FORMAT.format(dateTime.toDate())
    }
}