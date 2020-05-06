/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.poas.patientassistant.client.di.ViewModelKey
import ru.poas.patientassistant.client.patient.ui.glossary.GlossaryViewModel

@Module
abstract class GlossaryModule {

    @Binds
    @IntoMap
    @ViewModelKey(GlossaryViewModel::class)
    abstract fun bindViewModel(viewModel: GlossaryViewModel): ViewModel
}