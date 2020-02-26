package ru.poas.patientassistant.client.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import ru.poas.patientassistant.client.vo.Glossary

interface GlossaryService {

    @Headers("Accept-Encoding: identity")
    @GET("glossary/{recommendationId}")
    suspend fun getGlossary(@Header("Authorization") credentials: String,
                            @Path("recommendationId") recommendationId: Long): Response<Glossary>
}

/**
 * Main entry point for network access
 */
object GlossaryNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkUrlConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val glossaryService = retrofit.create(GlossaryService::class.java)
}