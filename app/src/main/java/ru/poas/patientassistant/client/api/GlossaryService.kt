package ru.poas.patientassistant.client.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import ru.poas.patientassistant.client.vo.Glossary

interface GlossaryService {

    @GET("glossary/visible")
    suspend fun getGlossary(@Header("Authorization") credentials: String): Response<List<Glossary>>
}

/**
 * Main entry point for network access
 */
object GlossaryNetwork {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkUrlConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val glossaryService = retrofit.create(GlossaryService::class.java)
}