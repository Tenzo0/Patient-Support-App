/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.login.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.alexey.patientassistant.client.utils.NetworkUrlConstants
import ru.alexey.patientassistant.client.login.vo.User

/**
 * A retrofit service to fetch a user
 */
interface AuthService {

    @GET("users/login")
    suspend fun login(@Header("Authorization") credentials: String): Response<User>

    @FormUrlEncoded
    @PUT("users/password")
    suspend fun updatePassword(@Header("Authorization") credentials: String,
                               @Field("password") password: String): Response<Unit>
}

/**
 * Main entry point for network access
 */
object UserNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkUrlConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val userService: AuthService = retrofit.create(
        AuthService::class.java)
}