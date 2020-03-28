/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjectionModule
import ru.poas.patientassistant.client.B2DocApplication
import ru.poas.patientassistant.client.patient.di.DrugsComponent
import ru.poas.patientassistant.client.patient.di.GlossaryComponent
import ru.poas.patientassistant.client.patient.di.RecommendationsComponent
import ru.poas.patientassistant.client.receivers.BootReceiver
import javax.inject.Singleton

/**
 * Main component for the application.
 */
@Singleton
@Component(
    modules = [
        AppModule::class,
        AndroidInjectionModule::class,
        ViewModelBuilderModule::class,
        AppWorkerModule::class,
        PatientSubcomponentsModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun inject(application: B2DocApplication)
    fun inject(bootReceiver: BootReceiver)

    fun glossaryComponent(): GlossaryComponent.Factory
    fun recommendationsComponent(): RecommendationsComponent.Factory
    fun drugsComponent(): DrugsComponent.Factory
}

@Module(
    subcomponents = [
        GlossaryComponent::class,
        RecommendationsComponent::class,
        DrugsComponent::class
    ]
)
object PatientSubcomponentsModule