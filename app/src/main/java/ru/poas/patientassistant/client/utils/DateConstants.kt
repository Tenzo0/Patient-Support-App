package ru.poas.patientassistant.client.utils

import java.text.SimpleDateFormat
import java.util.*

object DateConstants{
    val DATABASE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale("ru", "RU"))
    val DATABASE_TIME_FORMAT = SimpleDateFormat("HH:mm:ss", Locale("ru", "RU")).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
}