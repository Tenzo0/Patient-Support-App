/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.login.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import ru.poas.patientassistant.client.utils.NetworkUrlConstants

interface SyncService {

    @GET("server/time/get")
    suspend fun getServerTime(@Header("Authorization") credentials: String): Response<String>
}

object SyncNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkUrlConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val syncService: SyncService = retrofit.create(SyncService::class.java)
}