package ru.poas.patientassistant.client.patient.domain

import android.os.Parcel
import android.os.Parcelable

class RecommendationNotificationItem(
    val id: Long = 0,
    val day: Int = 0,
    val version: Long = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(day)
        parcel.writeLong(version)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecommendationNotificationItem> {
        override fun createFromParcel(parcel: Parcel): RecommendationNotificationItem {
            return RecommendationNotificationItem(parcel)
        }

        override fun newArray(size: Int): Array<RecommendationNotificationItem?> {
            return arrayOfNulls(size)
        }
    }
}