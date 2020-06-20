/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun crossfadeViews(revealedView: View, hidedView: View) {
    hideView(hidedView)
    revealView(revealedView)
}

fun hideView(view: View) {
    if (view.alpha != 0f) {
        view.animation?.cancel()
        view.animate()
            .alpha(0f)
            .setDuration(ANIMATION_DURATION)
    }
}

fun revealView(view: View) {
    view.animation?.cancel()
    view.apply {
        animate()
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
    }
}

const val ANIMATION_DURATION = 200L