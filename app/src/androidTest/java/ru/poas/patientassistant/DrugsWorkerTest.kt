/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertSame
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.poas.patientassistant.client.di.AppWorkerFactory
import ru.poas.patientassistant.client.patient.workers.DrugsWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class DrugsWorkerTest {
    private lateinit var context: Context
    @Inject
    lateinit var workerFactory: AppWorkerFactory

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        DaggerTestAppComponent.factory().create(context).inject(this)
    }

    @Test
    fun testWorker() {
        // Kotlin code can use the TestListenableWorkerBuilder extension to
        // build the ListenableWorker
        val worker = TestListenableWorkerBuilder<DrugsWorker>(context)
            .setWorkerFactory(workerFactory)
            .build() as DrugsWorker
        runBlocking {
            val result = worker.doWork()
            //assertSame(true, result)
            assertThat(result, `is`(Result.success()))
        }
    }

    @Test
    fun initialDelay() {
        val worker = TestListenableWorkerBuilder<DrugsWorker>(context)
            .setWorkerFactory(workerFactory)
            .build() as DrugsWorker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()
        val repeatingRequest = PeriodicWorkRequestBuilder<DrugsWorker>(
            15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(Result.success()))
        }

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "B2Doc drugs list and notifications update",
            ExistingPeriodicWorkPolicy.REPLACE,
            repeatingRequest
        ).result.get()

        val workInfo = WorkManager.getInstance(context).getWorkInfoById(repeatingRequest.id).get()
        val outputData = workInfo.outputData
        Log.d("worker", outputData.toString())
    }
}
