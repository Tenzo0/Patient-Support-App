/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.utils

import android.content.Context
import okhttp3.Credentials
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.MutableDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import ru.poas.patientassistant.client.login.api.SyncNetwork.syncService
import ru.poas.patientassistant.client.preferences.DatePreferences
import ru.poas.patientassistant.client.preferences.UserPreferences
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

object DateUtils{
    private const val DATABASE_DATE_PATTERN = "yyyy-MM-dd"
    private const val DATABASE_TIME_PATTERN = "HH:mm:ss"
    private const val DATABASE_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    val databaseSimpleDateFormat = SimpleDateFormat(DATABASE_DATE_PATTERN, Locale("ru", "RU"))
    val databaseSimpleTimeFormat = SimpleDateFormat(DATABASE_TIME_PATTERN, Locale("ru", "RU"))
        .apply { timeZone = TimeZone.getTimeZone("GMT") }
    val databaseSimpleDateTimeFormat = SimpleDateFormat(DATABASE_DATETIME_PATTERN, Locale("ru", "RU"))
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

    suspend fun syncDateWithServer(context: Context) {
        try {
            val date = databaseSimpleDateFormat.format(
                databaseSimpleDateTimeFormat
                    .parse(
                        syncService.getServerTime(
                            Credentials.basic(
                                UserPreferences.getPhone()!!,
                                UserPreferences.getPassword()!!
                            )
                        )
                            .body()!!.take(19)
                    )!!
            )
            DatePreferences.init(context)
            DatePreferences.setActualServerDate(date)
        }
        catch (e: NullPointerException) {
            Timber.e("syncDateWithServer null pointer exception")
        }
    }

    fun isDateInRangeOfCurrent(rangeInMinutes: Int, dateTime: String): Boolean {
        val currentDate = MutableDateTime.now()
        val top = currentDate.apply { addMinutes(rangeInMinutes) }
        val dateTime = DateTime(databaseSimpleDateTimeFormat.parse(dateTime))
        return !dateTime.isAfter(top) && !dateTime.isBefore(dateTime)
    }

    fun isDateIsGreatOrEqualThatCurrent(dateTime: String): Boolean {
        val dateTime = DateTime(databaseSimpleDateTimeFormat.parse(dateTime))
        return !dateTime.isAfterNow
    }
}