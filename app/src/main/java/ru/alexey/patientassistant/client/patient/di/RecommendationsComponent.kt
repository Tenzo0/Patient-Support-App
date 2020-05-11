/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.di

import dagger.Subcomponent
import ru.alexey.patientassistant.client.patient.ui.recommendations.RecommendationsFragment

@Subcomponent(modules = [RecommendationsModule::class])
interface RecommendationsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): RecommendationsComponent
    }

    fun inject(fragment: RecommendationsFragment)
}