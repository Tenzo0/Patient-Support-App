package ru.poas.patientassistant.client.patient.domain

import android.os.Parcel
import android.os.Parcelable
import ru.poas.patientassistant.client.utils.DateConstants.DATABASE_DATE_FORMAT
import ru.poas.patientassistant.client.utils.DateConstants.DATABASE_TIME_FORMAT
import java.util.*

data class DrugItem(
    val id: Long,
    val dose: Double,
    val doseTypeName: String,
    val dateOfMedicationReception: String,
    val timeOfMedicationReception: String,
    val name: String,
    val description: String,
    val manufacturer: String
) : Parcelable {
    constructor(parcel: Parcel): this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeLong(id)
            writeDouble(dose)
            writeString(doseTypeName)
            writeString(dateOfMedicationReception)
            writeString(timeOfMedicationReception)
            writeString(name)
            writeString(description)
            writeString(manufacturer)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DrugItem> {
        override fun createFromParcel(parcel: Parcel): DrugItem {
            return DrugItem(parcel)
        }

        override fun newArray(size: Int): Array<DrugItem?> {
            return arrayOfNulls(size)
        }
    }
}

fun List<DrugItem>.drugsStartFromDate(date: Date): List<DrugItem> =
    this.filter { drug ->
        val drugDate = DATABASE_DATE_FORMAT.parse(drug.dateOfMedicationReception)
        val drugTime = DATABASE_TIME_FORMAT.parse(drug.timeOfMedicationReception)

        val isDrugDateBeforeOrEqualCurrentDate = if (drugDate != null && drugTime != null) {
            (drugDate.time + drugTime.time) >= date.time
        } else false

        //filter
        isDrugDateBeforeOrEqualCurrentDate
    }