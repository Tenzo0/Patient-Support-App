/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun crossfadeViews(revealedView: View, hidedView: View) {
    revealNotVisibleView(revealedView)
    hideVisibleView(hidedView)
}

fun hideView(view: View) {
    // Animate the loading view to 0% opacity. After the animation ends,
    // set its visibility to GONE
    view.animate()
        .alpha(0f)
        .setDuration(ANIMATION_DURATION)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })
}

fun hideVisibleView(view: View) {
    if(view.visibility == View.VISIBLE) {
        hideView(view)
    }
}

fun revealView(view: View) {
    view.apply {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        alpha = 0f
        visibility = View.VISIBLE

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        animate()
            .alpha(1f)
            .setDuration(ANIMATION_DURATION)
            .setListener(null)
    }
}

fun revealNotVisibleView(view: View) {
    if(view.visibility != View.VISIBLE) {
        revealView(view)
    }
}

const val ANIMATION_DURATION = 250L