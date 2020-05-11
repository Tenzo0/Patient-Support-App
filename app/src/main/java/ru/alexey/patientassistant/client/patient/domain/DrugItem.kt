/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.domain

import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import ru.alexey.patientassistant.client.utils.DateUtils
import java.util.*

data class DrugItem(
    val id: Long = 0,
    val dose: Double = 0.0,
    val doseTypeName: String = "",
    val dateOfDrugReception: String = "",
    val timeOfDrugReception: String = "",
    val drugUnitId: Long = 0,
    val name: String = "",
    val description: String = "",
    val manufacturer: String = "",
    val realDateTimeOfMedicationReception: String?
)

fun DrugItem.asNotificationItem(version: Long): DrugNotificationItem = DrugNotificationItem(
    id,
    dose,
    doseTypeName,
    dateOfDrugReception,
    timeOfDrugReception,
    name,
    version
)

fun List<DrugItem>.drugsStartFromDate(date: Date): List<DrugItem> =
    this.filter { drug ->
        val drugDate = DateTime.parse(drug.dateOfDrugReception, DateUtils.databaseDateFormatter)
        val drugTime = DateTime.parse(drug.timeOfDrugReception, DateUtils.databaseTimeFormatter)
        val drugDateTime = MutableDateTime()
            .apply {
                setDate(drugDate)
                setTime(drugTime)
            }.toDate()

        //filter
        drugDateTime >= date
    }