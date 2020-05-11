/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.alexey.patientassistant.client.utils.NetworkUrlConstants
import ru.alexey.patientassistant.client.patient.vo.Recommendation
import ru.alexey.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.alexey.patientassistant.client.patient.vo.UserRecommendation

/**
 * A retrofit service to fetch a user
 */
interface RecommendationService {

    @GET("recommendations/patient/{patientId}")
    suspend fun getUserRecommendationsByPatientId(@Header("Authorization") credentials: String,
                                                  @Path("patientId") patientId: Long): Response<UserRecommendation>

    @GET("recommendationFull/RecommendationId/{id}")
    suspend fun getRecommendationListById(@Header("Authorization") credentials: String,
                                          @Path("id") id: Long): Response<List<Recommendation>>

    @PUT("users/confirm/")
    suspend fun putConfirmRecommendationKey(@Header("Authorization") credentials: String,
                                            @Body patientConfirmKey: RecommendationConfirmKey): Response<Unit>
}

/**
 * Main entry point for network access
 */
object RecommendationNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkUrlConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val recommendationService: RecommendationService = retrofit.create(
        RecommendationService::class.java)
}