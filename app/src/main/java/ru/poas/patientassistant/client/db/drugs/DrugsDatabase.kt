package ru.poas.patientassistant.client.db.drugs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DrugsEntity::class], version = 1)
abstract class DrugsDatabase : RoomDatabase() {
    abstract val drugsDao: DrugsDao
}

private lateinit var INSTANCE: DrugsDatabase

fun getGlossaryDatabase(context: Context): DrugsDatabase {
    synchronized(DrugsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                DrugsDatabase::class.java,
                "glossary").build()
        }
    }
    return INSTANCE
}