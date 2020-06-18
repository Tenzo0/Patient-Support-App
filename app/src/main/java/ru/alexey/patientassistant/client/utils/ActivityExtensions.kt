/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import timber.log.Timber

fun Activity.hideKeyboard() {
    try {
        var view: View? = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        (this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        Timber.e(e)
    }
}