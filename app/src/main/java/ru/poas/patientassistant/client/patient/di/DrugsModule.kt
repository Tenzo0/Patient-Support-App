package ru.poas.patientassistant.client.patient.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.poas.patientassistant.client.di.ViewModelKey
import ru.poas.patientassistant.client.patient.ui.drugs.DrugsViewModel

@Module
abstract class DrugsModule {

    @Binds
    @IntoMap
    @ViewModelKey(DrugsViewModel::class)
    abstract fun bindViewModel(viewModel: DrugsViewModel): ViewModel
}