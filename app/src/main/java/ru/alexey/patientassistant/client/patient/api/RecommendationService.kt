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
import retrofit2.http.*
import ru.alexey.patientassistant.client.BuildConfig
import ru.alexey.patientassistant.client.utils.NetworkUrlConstants
import ru.alexey.patientassistant.client.patient.vo.Recommendation
import ru.alexey.patientassistant.client.patient.vo.RecommendationConfirmKey
import ru.alexey.patientassistant.client.patient.vo.UserRecommendation
import ru.alexey.patientassistant.client.utils.NetworkUrlConstants.BASE_URL
import timber.log.Timber

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
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .apply {
            if (BuildConfig.DEBUG)
                client(OkHttpClient.Builder().addInterceptor(FakeRecommendationNetworkInterceptor).build())
        }
        .build()

    val recommendationService: RecommendationService = retrofit.create(
        RecommendationService::class.java)
}

private object FakeRecommendationNetworkInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val code = 200
        Timber.i("${chain.request().url().toString()}")
        var message = when(chain.request().url().toString()) {
            "${BASE_URL}recommendations/patient/1" -> {
                 getUserRecommendationsByPatientIdResponse
            }
            "${BASE_URL}recommendationFull/RecommendationId/1" -> {
                getRecommendationListByIdResponse
            }
            else -> ""
        }

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

    const val getUserRecommendationsByPatientIdResponse =
        "{" +
                "\"operationDate\":\"2020-06-17\"," +
                "\"recommendationId\":1" +
        "}"
    

    const val getRecommendationListByIdResponse =
        "[" +
            "{" +
                "\"id\":0," +
                "\"recommendationId\":1," +
                "\"day\":0," +

                "\"recommendationUnit\":" +
                "{" +
                    "\"id\":0," +
                    "\"content\":\"\"," +
                    "\"importantContent\":\"Сегодня была операция\"," +
                    "\"message\":\"\"" +
                "}" +
            "}," +
        "{" +
            "\"id\":1," +
            "\"recommendationId\":1," +
            "\"day\":1," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":1," +
                "\"content\":\"I.\n" +
                "В первые сутки после госпитализации чаще всего назначается строгий постельный режим, который со второго дня расширяют и дополняют лечебной гимнастикой, состоящей из индивидуально подобранных упражнений. Постепенно пациент может увеличивать время, когда он может сидеть, стоять, а позже и ходить по отделению. Обычно через 7-18 дней разрешаются прогулки до 2-3 км в медленном темпе и занятия на велотренажере.\n" +
                "\n" +
                "В конце этапа рассчитывается уровень сердечно-сосудистого риска. Для этого проводится повторное электрокардиографическое обследование, определяется выраженность атеросклеротических изменений, оцениваются способности сердца на ЭхоКГ (ультразвуковое исследование сердца).\n" +
                "\n" +
                "II. \n" +
                "После перевода в реабилитационное отделение или санаторий проводят пробы с физической нагрузкой. По их результатам выбирают оптимальную физическую нагрузку и ее вид. Также учитывают методы восстановления кровотока в коронарных артериях, скорость освоения различных ступеней активности на предыдущем этапе и общее состояние.\n" +
                "\n" +
                "В комплекс лечебной физкультуры (ЛФК) постепенно включают ходьбу в более быстром темпе, плавание, занятия на тренажерах.\n" +
                "\n" +
                "III.  \n" +
                "На амбулаторном этапе определяющим является функциональный класс ишемической болезни сердца (ИБС), который устанавливают с помощью исследования, в ходе которого оценивается рост потребления кислорода при физической нагрузке (велоэргометрия, тредмил тест — ходьба по «бегущей дороге»).\n" +
                "\n" +
                "Этот тест отражает не только работу сердечно-сосудистой системы, но и дыхательной. \n" +
                "\n" +
                "В зависимости от функционального класса ИБС лечебная физкультура проводится в тренирующем или щадящем режиме.\n" +
                "\n" +
                "Исследования показывали, что чем дольше проводится физическая реабилитация, тем лучше отдаленные результаты – в большей степени снижается риск повторного ИМ и смерти.\n" +
                "\n\"," +
                "\"importantContent\":\"Это важно\"," +
                "\"message\":\"Посмотрите свою первую рекомендацию\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":2," +
            "\"recommendationId\":1," +
            "\"day\":2," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":2," +
                "\"content\":\"Отдыхать\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Позавчера была операция\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":3," +
            "\"recommendationId\":1," +
            "\"day\":3," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":3," +
                "\"content\":\"\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":4," +
            "\"recommendationId\":1," +
            "\"day\":4," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":1," +
                "\"content\":\"I.\n" +
                "В первые сутки после госпитализации чаще всего назначается строгий постельный режим, который со второго дня расширяют и дополняют лечебной гимнастикой, состоящей из индивидуально подобранных упражнений. Постепенно пациент может увеличивать время, когда он может сидеть, стоять, а позже и ходить по отделению. Обычно через 7-18 дней разрешаются прогулки до 2-3 км в медленном темпе и занятия на велотренажере.\n" +
                "\n" +
                "В конце этапа рассчитывается уровень сердечно-сосудистого риска. Для этого проводится повторное электрокардиографическое обследование, определяется выраженность атеросклеротических изменений, оцениваются способности сердца на ЭхоКГ (ультразвуковое исследование сердца).\n" +
                "\n" +
                "II. \n" +
                "После перевода в реабилитационное отделение или санаторий проводят пробы с физической нагрузкой. По их результатам выбирают оптимальную физическую нагрузку и ее вид. Также учитывают методы восстановления кровотока в коронарных артериях, скорость освоения различных ступеней активности на предыдущем этапе и общее состояние.\n" +
                "\n" +
                "В комплекс лечебной физкультуры (ЛФК) постепенно включают ходьбу в более быстром темпе, плавание, занятия на тренажерах.\n" +
                "\n" +
                "III.  \n" +
                "На амбулаторном этапе определяющим является функциональный класс ишемической болезни сердца (ИБС), который устанавливают с помощью исследования, в ходе которого оценивается рост потребления кислорода при физической нагрузке (велоэргометрия, тредмил тест — ходьба по «бегущей дороге»).\n" +
                "\n" +
                "Этот тест отражает не только работу сердечно-сосудистой системы, но и дыхательной. \n" +
                "\n" +
                "В зависимости от функционального класса ИБС лечебная физкультура проводится в тренирующем или щадящем режиме.\n" +
                "\n" +
                "Исследования показывали, что чем дольше проводится физическая реабилитация, тем лучше отдаленные результаты – в большей степени снижается риск повторного ИМ и смерти.\n" +
                "\n\"," +
                "\"importantContent\":\"Это важно\"," +
                "\"message\":\"Посмотрите свою первую рекомендацию\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":5," +
            "\"recommendationId\":1," +
            "\"day\":5," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":2," +
                "\"content\":\"Отдыхать\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Позавчера была операция\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":6," +
            "\"recommendationId\":1," +
            "\"day\":6," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":1," +
                "\"content\":\"I.\n" +
                "В первые сутки после госпитализации чаще всего назначается строгий постельный режим, который со второго дня расширяют и дополняют лечебной гимнастикой, состоящей из индивидуально подобранных упражнений. Постепенно пациент может увеличивать время, когда он может сидеть, стоять, а позже и ходить по отделению. Обычно через 7-18 дней разрешаются прогулки до 2-3 км в медленном темпе и занятия на велотренажере.\n" +
                "\n" +
                "В конце этапа рассчитывается уровень сердечно-сосудистого риска. Для этого проводится повторное электрокардиографическое обследование, определяется выраженность атеросклеротических изменений, оцениваются способности сердца на ЭхоКГ (ультразвуковое исследование сердца).\n" +
                "\n" +
                "II. \n" +
                "После перевода в реабилитационное отделение или санаторий проводят пробы с физической нагрузкой. По их результатам выбирают оптимальную физическую нагрузку и ее вид. Также учитывают методы восстановления кровотока в коронарных артериях, скорость освоения различных ступеней активности на предыдущем этапе и общее состояние.\n" +
                "\n" +
                "В комплекс лечебной физкультуры (ЛФК) постепенно включают ходьбу в более быстром темпе, плавание, занятия на тренажерах.\n" +
                "\n" +
                "III.  \n" +
                "На амбулаторном этапе определяющим является функциональный класс ишемической болезни сердца (ИБС), который устанавливают с помощью исследования, в ходе которого оценивается рост потребления кислорода при физической нагрузке (велоэргометрия, тредмил тест — ходьба по «бегущей дороге»).\n" +
                "\n" +
                "Этот тест отражает не только работу сердечно-сосудистой системы, но и дыхательной. \n" +
                "\n" +
                "В зависимости от функционального класса ИБС лечебная физкультура проводится в тренирующем или щадящем режиме.\n" +
                "\n" +
                "Исследования показывали, что чем дольше проводится физическая реабилитация, тем лучше отдаленные результаты – в большей степени снижается риск повторного ИМ и смерти.\n" +
                "\n\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Посмотрите свою первую рекомендацию\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":7," +
            "\"recommendationId\":1," +
            "\"day\":7," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":2," +
                "\"content\":\"Отдыхать\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Позавчера была операция\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":8," +
            "\"recommendationId\":1," +
            "\"day\":8," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":1," +
                "\"content\":\"I.\n" +
                "В первые сутки после госпитализации чаще всего назначается строгий постельный режим, который со второго дня расширяют и дополняют лечебной гимнастикой, состоящей из индивидуально подобранных упражнений. Постепенно пациент может увеличивать время, когда он может сидеть, стоять, а позже и ходить по отделению. Обычно через 7-18 дней разрешаются прогулки до 2-3 км в медленном темпе и занятия на велотренажере.\n" +
                "\n" +
                "В конце этапа рассчитывается уровень сердечно-сосудистого риска. Для этого проводится повторное электрокардиографическое обследование, определяется выраженность атеросклеротических изменений, оцениваются способности сердца на ЭхоКГ (ультразвуковое исследование сердца).\n" +
                "\n" +
                "II. \n" +
                "После перевода в реабилитационное отделение или санаторий проводят пробы с физической нагрузкой. По их результатам выбирают оптимальную физическую нагрузку и ее вид. Также учитывают методы восстановления кровотока в коронарных артериях, скорость освоения различных ступеней активности на предыдущем этапе и общее состояние.\n" +
                "\n" +
                "В комплекс лечебной физкультуры (ЛФК) постепенно включают ходьбу в более быстром темпе, плавание, занятия на тренажерах.\n" +
                "\n" +
                "III.  \n" +
                "На амбулаторном этапе определяющим является функциональный класс ишемической болезни сердца (ИБС), который устанавливают с помощью исследования, в ходе которого оценивается рост потребления кислорода при физической нагрузке (велоэргометрия, тредмил тест — ходьба по «бегущей дороге»).\n" +
                "\n" +
                "Этот тест отражает не только работу сердечно-сосудистой системы, но и дыхательной. \n" +
                "\n" +
                "В зависимости от функционального класса ИБС лечебная физкультура проводится в тренирующем или щадящем режиме.\n" +
                "\n" +
                "Исследования показывали, что чем дольше проводится физическая реабилитация, тем лучше отдаленные результаты – в большей степени снижается риск повторного ИМ и смерти.\n" +
                "\n\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Посмотрите свою первую рекомендацию\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":9," +
            "\"recommendationId\":1," +
            "\"day\":9," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":2," +
                "\"content\":\"Отдыхать\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Позавчера была операция\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":10," +
            "\"recommendationId\":1," +
            "\"day\":10," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":1," +
                "\"content\":\"I.\n" +
                "В первые сутки после госпитализации чаще всего назначается строгий постельный режим, который со второго дня расширяют и дополняют лечебной гимнастикой, состоящей из индивидуально подобранных упражнений. Постепенно пациент может увеличивать время, когда он может сидеть, стоять, а позже и ходить по отделению. Обычно через 7-18 дней разрешаются прогулки до 2-3 км в медленном темпе и занятия на велотренажере.\n" +
                "\n" +
                "В конце этапа рассчитывается уровень сердечно-сосудистого риска. Для этого проводится повторное электрокардиографическое обследование, определяется выраженность атеросклеротических изменений, оцениваются способности сердца на ЭхоКГ (ультразвуковое исследование сердца).\n" +
                "\n" +
                "II. \n" +
                "После перевода в реабилитационное отделение или санаторий проводят пробы с физической нагрузкой. По их результатам выбирают оптимальную физическую нагрузку и ее вид. Также учитывают методы восстановления кровотока в коронарных артериях, скорость освоения различных ступеней активности на предыдущем этапе и общее состояние.\n" +
                "\n" +
                "В комплекс лечебной физкультуры (ЛФК) постепенно включают ходьбу в более быстром темпе, плавание, занятия на тренажерах.\n" +
                "\n" +
                "III.  \n" +
                "На амбулаторном этапе определяющим является функциональный класс ишемической болезни сердца (ИБС), который устанавливают с помощью исследования, в ходе которого оценивается рост потребления кислорода при физической нагрузке (велоэргометрия, тредмил тест — ходьба по «бегущей дороге»).\n" +
                "\n" +
                "Этот тест отражает не только работу сердечно-сосудистой системы, но и дыхательной. \n" +
                "\n" +
                "В зависимости от функционального класса ИБС лечебная физкультура проводится в тренирующем или щадящем режиме.\n" +
                "\n" +
                "Исследования показывали, что чем дольше проводится физическая реабилитация, тем лучше отдаленные результаты – в большей степени снижается риск повторного ИМ и смерти.\n" +
                "\n\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Посмотрите свою первую рекомендацию\"" +
            "}" +
        "}," +
        "{" +
            "\"id\":11," +
            "\"recommendationId\":1," +
            "\"day\":11," +

            "\"recommendationUnit\":" +
            "{" +
                "\"id\":2," +
                "\"content\":\"Отдыхать\"," +
                "\"importantContent\":\"\"," +
                "\"message\":\"Позавчера была операция\"" +
            "}" +
        "}" +
        "]"
}