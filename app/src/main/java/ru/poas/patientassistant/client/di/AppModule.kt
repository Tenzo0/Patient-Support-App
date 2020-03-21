package ru.poas.patientassistant.client.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import ru.poas.patientassistant.client.patient.db.glossary.GlossaryDatabase
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationsDatabase
import ru.poas.patientassistant.client.patient.repository.GlossaryRepository
import ru.poas.patientassistant.client.patient.repository.RecommendationsRepository
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
    fun bindGRecommendationsRepository(database: RecommendationsDatabase): RecommendationsRepository {
        return RecommendationsRepository(database)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideGlossaryDatabase(context: Context): GlossaryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GlossaryDatabase::class.java,
            "Glossary.db"
        ).build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRecommendationsDatabase(context: Context): RecommendationsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RecommendationsDatabase::class.java,
            "Recommendations.db"
        ).build()
    }


    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}