/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.db.drugs

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DrugEntity::class], version = 1)
abstract class DrugsDatabase : RoomDatabase() {
    abstract val drugsDao: DrugsDao
}