/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import ru.alexey.patientassistant.client.BuildConfig
import ru.alexey.patientassistant.client.patient.vo.Medicament
import ru.alexey.patientassistant.client.utils.NetworkUrlConstants

interface DrugsService {

    @GET("patient/medicament/get/all/units")
    suspend fun getAllUnitsAssignedToPatient(@Header("Authorization") credentials: String):
            Response<List<Medicament>>

    @GET("patient/medicament/get/units/{date}")
    suspend fun getAllUnitsAssignedToPatientAnyDate(
        @Header("Authorization") credentials: String,
        @Path("date") date: String
    ): Response<List<Medicament>>

    @GET("patient/medicament/get/units/{firstDate}/{lastDate}")
    suspend fun getAllUnitsAssignedToPatientByDateRange(
        @Header("Authorization") credentials: String,
        @Path("firstDate") firstDate: String,
        @Path("lastDate") lastDate: String
    ): Response<List<Medicament>>

    @POST("patient/medicament/unit/{unitID}/confirm")
    suspend fun confirmMedicamentUnit(
        @Header("Authorization") credentials: String,
        @Path("unitID") unitID: Long
    ): Response<Unit>
}

object DrugsNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkUrlConstants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .apply {
            if (BuildConfig.DEBUG)
                client(OkHttpClient.Builder().addInterceptor(FakeDrugsNetworkInterceptor).build())
        }
        .build()

    val drugsService: DrugsService = retrofit.create(DrugsService::class.java)
}

private object FakeDrugsNetworkInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var message = response
        val code = 200
        if (chain.request().url().toString().startsWith("patient/medicament/get/")) {
        }
        //else message = ""
        return okhttp3.Response.Builder()
            .code(code)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_0)
            .body(
                ResponseBody
                    .create(
                        MediaType.parse("application/json"),
                        message.toByteArray()
                    )
            )
            .message(message)
            .build()
    }

    const val response =
        "[" +
            "{" +
                "\"id\":1," +

                //Medicament Unit
                "\"medicament\":" +
                    "{" +
                        "\"id\":1," +
                        "\"name\":\"Валерианы настойка\"," +
                        "\"description\":\"Средство растительного происхождения\"," +
                        "\"manufacturer\":\"Россия, НИИ МЕДИЦИНЫ И СТАНДАРТИЗАЦИИ\"" +
                    "}," +

                "\"dose\":1.0," +
                "\"doseTypeName\":\"флакон\"," +
                "\"dateOfMedicationReception\":\"2020-06-18\"," +
                "\"timeOfMedicationReception\":\"10:00:00\"" +
            "}," +

            "{" +
                "\"id\":2," +

                    //Medicament Unit
                    "\"medicament\":" +
                    "{" +
                        "\"id\":2," +
                        "\"name\":\"Глицин\"," +
                        "\"description\":\"Регулятор обмена веществ\"," +
                        "\"manufacturer\":\"Россия, БИОТИКИ МНПК\"" +
                    "}," +

                "\"dose\":100.0," +
                "\"doseTypeName\":\"мг\"," +
                "\"dateOfMedicationReception\":\"2020-06-18\"," +
                "\"timeOfMedicationReception\":\"10:30:00\"" +

            "}," +

            "{" +
                "\"id\":3," +

                    //Medicament Unit
                    "\"medicament\":" +
                    "{" +
                        "\"id\":3," +
                        "\"name\":\"Пустырника настойка\"," +
                        "\"description\":\"Успокоительное\"," +
                        "\"manufacturer\":\"Ивановская фармацевтическая фабрика\"" +
                    "}," +

                "\"dose\":1.0," +
                "\"doseTypeName\":\"таб.\"," +
                "\"dateOfMedicationReception\":\"2020-06-19\"," +
                "\"timeOfMedicationReception\":\"13:20:00\"" +
            "}," +

            "{" +
                "\"id\":4," +

                    //Medicament Unit
                    "\"medicament\":" +
                    "{" +
                        "\"id\":4," +
                        "\"name\":\"Пустырник\"," +
                        "\"description\":\"Успокоительное\"," +
                        "\"manufacturer\":\"Ивановская фармацевтическая фабрика\"" +
                    "}," +

                "\"dose\":100.0," +
                "\"doseTypeName\":\"мл\"," +
                "\"dateOfMedicationReception\":\"2020-06-17\"," +
                "\"timeOfMedicationReception\":\"11:00:00\"," +

                //Date and time when the user send info about medicine taking
                "\"realDateTimeOfMedicationReception\":\"2020-06-17T11:20:00\"" +
            "}," +


            "{" +
                "\"id\":5," +

                //Medicament Unit
                "\"medicament\":" +
                    "{" +
                        "\"id\":1," +
                        "\"name\":\"Валерианы настойка\"," +
                        "\"description\":\"Средство растительного происхождения\"," +
                        "\"manufacturer\":\"Россия, НИИ МЕДИЦИНЫ И СТАНДАРТИЗАЦИИ\"" +
                    "}," +

                "\"dose\":1.0," +
                "\"doseTypeName\":\"флакон\"," +
                "\"dateOfMedicationReception\":\"2020-06-20\"," +
                "\"timeOfMedicationReception\":\"10:00:00\"" +
            "}," +

            "{" +
                "\"id\":6," +

                    //Medicament Unit
                    "\"medicament\":" +
                    "{" +
                        "\"id\":2," +
                        "\"name\":\"Глицин\"," +
                        "\"description\":\"Регулятор обмена веществ\"," +
                        "\"manufacturer\":\"Россия, БИОТИКИ МНПК\"" +
                    "}," +

                "\"dose\":100.0," +
                "\"doseTypeName\":\"мг\"," +
                "\"dateOfMedicationReception\":\"2020-06-20\"," +
                "\"timeOfMedicationReception\":\"10:30:00\"" +

            "}" +
        "]"
}