package ru.poas.patientassistant.client.vo
/*
import com.squareup.moshi.Json
import ru.poas.patientassistant.client.db.recommendations.RecommendationEntity

data class Recommendation(
    @Json(name = "description") val description: String,
    @Json(name = "dischargeDay") val dischargeDay: Int,
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "updateAt") val updateAt: String
)

fun Recommendation.asDatabaseModel(): RecommendationEntity = RecommendationEntity(
    description,
    dischargeDay,
    id,
    name,
    updateAt
)

fun List<Recommendation>.asDatabaseModel(): List<RecommendationEntity> = map { it.asDatabaseModel() }*/