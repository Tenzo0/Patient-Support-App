package ru.poas.patientassistant.client.patient.domain

import ru.poas.patientassistant.client.utils.DateUtils.DATABASE_DATE_FORMAT
import ru.poas.patientassistant.client.utils.DateUtils.DATABASE_TIME_FORMAT
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
    val isAccepted: Boolean = false,
    val isNeededToAccept: Boolean = false
)

fun DrugItem.asNotificationItem(version: Long): DrugNotificationItem = DrugNotificationItem(
    id,
    dose,
    doseTypeName,
    name,
    description,
    manufacturer,
    version
)

fun List<DrugItem>.drugsStartFromDate(date: Date): List<DrugItem> =
    this.filter { drug ->
        val drugDate = DATABASE_DATE_FORMAT.parse(drug.dateOfDrugReception)
        val drugTime = DATABASE_TIME_FORMAT.parse(drug.timeOfDrugReception)

        val isDrugDateBeforeOrEqualCurrentDate = if (drugDate != null && drugTime != null) {
            (drugDate.time + drugTime.time) >= date.time
        } else false

        //filter
        isDrugDateBeforeOrEqualCurrentDate
    }