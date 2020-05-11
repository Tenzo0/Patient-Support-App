/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.di

import dagger.Subcomponent
import ru.alexey.patientassistant.client.patient.ui.drugs.DrugsFragment

@Subcomponent(modules = [DrugsModule::class])
interface DrugsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): DrugsComponent
    }

    fun inject(fragment: DrugsFragment)
}