package ru.poas.patientassistant.client.patient.db.drugs

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DrugEntity::class, DrugsAcceptanceEntity::class], version = 1)
abstract class DrugsDatabase : RoomDatabase() {
    abstract val drugsDao: DrugsDao
}