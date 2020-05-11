/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.di

import dagger.Subcomponent
import ru.alexey.patientassistant.client.patient.ui.glossary.GlossaryFragment

@Subcomponent(modules = [GlossaryModule::class])
interface GlossaryComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): GlossaryComponent
    }

    fun inject(fragment: GlossaryFragment)
}