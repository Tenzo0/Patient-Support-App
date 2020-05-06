/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.di

import dagger.Subcomponent
import ru.poas.patientassistant.client.patient.ui.drugs.DrugsFragment

@Subcomponent(modules = [DrugsModule::class])
interface DrugsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): DrugsComponent
    }

    fun inject(fragment: DrugsFragment)
}