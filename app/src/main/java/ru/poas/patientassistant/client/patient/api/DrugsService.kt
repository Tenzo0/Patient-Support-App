package ru.poas.patientassistant.client.patient.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import ru.poas.patientassistant.client.patient.vo.Medicament
import ru.poas.patientassistant.client.utils.NetworkUrlConstants

interface DrugsService {

    @GET("patient/medicament/get/all/units")
    suspend fun getAllUnitsAssignedToPatient(@Header("Authorization") credentials: String):
            Response<List<Medicament>>
}

/**
 * Main entry point for network access
 */
object DrugsNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkUrlConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val drugsService = retrofit.create(
        DrugsService::class.java)
}