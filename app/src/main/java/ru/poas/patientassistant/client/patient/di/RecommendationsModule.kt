package ru.poas.patientassistant.client.patient.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.poas.patientassistant.client.di.ViewModelKey
import ru.poas.patientassistant.client.patient.ui.recommendations.RecommendationsViewModel

@Module
abstract class RecommendationsModule {

    @Binds
    @IntoMap
    @ViewModelKey(RecommendationsViewModel::class)
    abstract fun bindViewModel(viewModel: RecommendationsViewModel): ViewModel
}