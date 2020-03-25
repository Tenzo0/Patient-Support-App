package ru.poas.patientassistant.client.patient.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.poas.patientassistant.client.di.ViewModelKey
import ru.poas.patientassistant.client.patient.repository.DrugsRepository
import ru.poas.patientassistant.client.patient.ui.drugs.DrugsAdapter
import ru.poas.patientassistant.client.patient.ui.drugs.DrugsViewModel
import javax.inject.Singleton

@Module(includes = [DrugsModule.DrugsViewModelModule::class])
object DrugsModule {

    @Module
    abstract class DrugsViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(DrugsViewModel::class)
        abstract fun bindViewModel(viewModel: DrugsViewModel): ViewModel
    }

    @JvmStatic
    @Provides
    fun provideDrugsAdapter(): DrugsAdapter {
        return DrugsAdapter()
    }
}