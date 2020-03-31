package ru.poas.patientassistant

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import ru.poas.patientassistant.client.di.AppModule
import ru.poas.patientassistant.client.di.AppWorkerModule
import javax.inject.Singleton

/**
 * Main component for the application.
 */
@Singleton
@Component(
    modules = [
        AppModule::class,
        AndroidInjectionModule::class,
        AppWorkerModule::class
    ]
)
interface TestAppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): TestAppComponent
    }

    fun inject(application: DrugsWorkerTest)
    fun inject(application: DrugsRepositoryTest)
}