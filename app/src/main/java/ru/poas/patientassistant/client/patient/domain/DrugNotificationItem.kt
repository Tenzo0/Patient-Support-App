package ru.poas.patientassistant.client.patient.domain

import android.os.Parcel
import android.os.Parcelable

class DrugNotificationItem (
    val id: Long = 0,
    val dose: Double = 0.0,
    val doseTypeName: String = "",
    val name: String = "",
    val description: String = "",
    val manufacturer: String = "",
    val version: Long = 0
): Parcelable {
    constructor(parcel: Parcel): this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeLong(id)
            writeDouble(dose)
            writeString(doseTypeName)
            writeString(name)
            writeString(description)
            writeString(manufacturer)
            writeLong(version)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DrugNotificationItem> {
        override fun createFromParcel(parcel: Parcel): DrugNotificationItem {
            return DrugNotificationItem(parcel)
        }

        override fun newArray(size: Int): Array<DrugNotificationItem?> {
            return arrayOfNulls(size)
        }
    }
}