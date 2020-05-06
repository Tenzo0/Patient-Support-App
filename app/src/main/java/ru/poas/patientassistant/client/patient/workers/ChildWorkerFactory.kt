/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.poas.patientassistant.client.patient.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}