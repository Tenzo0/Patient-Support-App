/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrugsRepositoryTest {

    private lateinit var context: Context
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        DaggerTestAppComponent.factory().create(context).inject(this)
    }

    @Test
    fun testRefresh() {

    }
}