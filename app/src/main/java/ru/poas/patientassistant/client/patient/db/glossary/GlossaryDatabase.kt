/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.db.glossary

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GlossaryEntity::class], version = 1, exportSchema = false)
abstract class GlossaryDatabase: RoomDatabase() {
    abstract val glossaryDao: GlossaryDao
}