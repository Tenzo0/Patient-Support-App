/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import ru.alexey.patientassistant.client.patient.db.drugs.DrugsDatabase
import ru.alexey.patientassistant.client.patient.db.glossary.GlossaryDatabase
import ru.alexey.patientassistant.client.patient.db.recommendations.RecommendationsDatabase
import ru.alexey.patientassistant.client.patient.repository.DrugsRepository
import ru.alexey.patientassistant.client.patient.repository.GlossaryRepository
import ru.alexey.patientassistant.client.patient.repository.RecommendationsRepository
import javax.inject.Singleton

@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun bindGlossaryRepository(database: GlossaryDatabase): GlossaryRepository {
        return GlossaryRepository(database)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun bindRecommendationsRepository(context: Context, database: RecommendationsDatabase): RecommendationsRepository {
        return RecommendationsRepository(context.applicationContext, database)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun bindDrugsRepository(context: Context, database: DrugsDatabase): DrugsRepository {
        return DrugsRepository(context.applicationContext, database)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideGlossaryDatabase(context: Context): GlossaryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GlossaryDatabase::class.java,
            "Glossary.db")
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRecommendationsDatabase(context: Context): RecommendationsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RecommendationsDatabase::class.java,
            "Recommendations.db")
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDrugsDatabase(context: Context): DrugsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DrugsDatabase::class.java,
            "Drugs.db")
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}