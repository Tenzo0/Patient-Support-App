package ru.poas.patientassistant.client.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Credentials
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.poas.patientassistant.client.vo.User

/**
 * A retrofit service to fetch a user
 */
interface UserService {

    @GET("users/login")
    suspend fun login(@Header("Authorization") credentials: String): Response<User>
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

    val userService = retrofit.create(UserService::class.java)
}