package ru.poas.patientassistant.client.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecommendationEntity::class], version = 1, exportSchema = false)
abstract class RecommendationsDatabase: RoomDatabase() {
    abstract val recommendationsDao: RecommendationsDao
}

private lateinit var INSTANCE: RecommendationsDatabase

fun getRecommendationsDatabase(context: Context): RecommendationsDatabase{
    synchronized(RecommendationsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                RecommendationsDatabase::class.java,
                "recommendations").build()
        }
    }
    return INSTANCE
}