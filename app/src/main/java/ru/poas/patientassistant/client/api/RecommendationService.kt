package ru.poas.patientassistant.client.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.poas.patientassistant.client.vo.UserRecommendation

/**
 * A retrofit service to fetch a user
 */
interface RecommendationService {

    @GET("recommendations/patient/{patientId}")
    suspend fun getUserRecommendationsByPatientId(@Header("Authorization") credentials: String,
                                                  @Path("patientId") patientId: Long): Response<UserRecommendation>
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

    val recommendationService = retrofit.create(RecommendationService::class.java)
}