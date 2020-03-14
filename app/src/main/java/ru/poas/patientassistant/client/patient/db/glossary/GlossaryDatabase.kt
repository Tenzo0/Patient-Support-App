package ru.poas.patientassistant.client.patient.db.glossary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GlossaryEntity::class], version = 1, exportSchema = false)
abstract class GlossaryDatabase: RoomDatabase() {
    abstract val glossaryDao: GlossaryDao
}

private lateinit var INSTANCE: GlossaryDatabase

fun getGlossaryDatabase(context: Context): GlossaryDatabase{
    synchronized(GlossaryDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                GlossaryDatabase::class.java,
                "glossary").build()
        }
    }
    return INSTANCE
}