package ru.poas.patientassistant.client.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjectionModule
import ru.poas.patientassistant.client.patient.di.GlossaryComponent
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
        PatientSubcomponentsModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun glossaryComponent(): GlossaryComponent.Factory
}

@Module(
    subcomponents = [GlossaryComponent::class]
)
object PatientSubcomponentsModule