/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.db.recommendations

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecommendationEntity::class, RecommendationConfirmKeyEntity::class], version = 1, exportSchema = false)
abstract class RecommendationsDatabase: RoomDatabase() {
    abstract val recommendationsDao: RecommendationsDao
}