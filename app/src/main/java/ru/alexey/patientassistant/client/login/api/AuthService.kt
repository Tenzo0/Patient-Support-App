/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.login.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.alexey.patientassistant.client.BuildConfig
import ru.alexey.patientassistant.client.utils.NetworkUrlConstants
import ru.alexey.patientassistant.client.login.vo.User
import ru.alexey.patientassistant.client.utils.NetworkUrlConstants.BASE_URL

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
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .apply {
            if (BuildConfig.DEBUG)
                client(OkHttpClient.Builder().addInterceptor(FakeUserNetworkInterceptor).build())
        }
        .build()

    val userService: AuthService = retrofit.create(
        AuthService::class.java)
}

private object FakeUserNetworkInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val code = 200
        if (chain.request().url().toString() == "${BASE_URL}users/password")
            isTemporaryPassword = false

        return okhttp3.Response.Builder()
            .code(code)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_0)
            .message("OK")
            .body(
                ResponseBody
                    .create(
                        MediaType.parse("application/json"),
                        response.toByteArray()
                    )
            )
            .build()
    }

    private var isTemporaryPassword = true

    val response
        get() =
            "{" +
                "\"id\":1," +
                "\"firstName\":\"Тестовые\"," +
                "\"lastName\":\"Данные\"," +
                "\"phone\":\"1234567890\"," +
                "\"roles\":" +
                    "[" +
                        "{" +
                            "\"description\":\"\"," +
                            "\"id\":0," +
                            "\"name\":\"ROLE_PATIENT\"" +
                        "}" +
                    "]," +
                "\"isTemporaryPassword\":$isTemporaryPassword" +
            "}"
}