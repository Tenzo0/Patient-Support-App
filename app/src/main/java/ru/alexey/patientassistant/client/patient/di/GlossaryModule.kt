/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.alexey.patientassistant.client.di.ViewModelKey
import ru.alexey.patientassistant.client.patient.ui.glossary.GlossaryViewModel

@Module
abstract class GlossaryModule {

    @Binds
    @IntoMap
    @ViewModelKey(GlossaryViewModel::class)
    abstract fun bindViewModel(viewModel: GlossaryViewModel): ViewModel
}