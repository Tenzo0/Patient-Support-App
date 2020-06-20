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
    view.animation?.cancel()
    // Animate the loading view to 0% opacity. After the animation ends,
    // set its visibility to GONE
    view.animate()
        .alpha(0f)
        .setDuration(ANIMATION_DURATION)
}

fun revealView(view: View) {
    view.animation?.cancel()
    view.apply {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        alpha = 0f

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        animate()
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
    }
}

const val ANIMATION_DURATION = 250L