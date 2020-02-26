package ru.poas.patientassistant.client.db.recommendations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import ru.poas.patientassistant.client.vo.UserRecommendation

/*@Entity(tableName = "recommendations_database")
data class RecommendationEntity(
    @ColumnInfo val description: String,
    @ColumnInfo val dischargeDay: Int,
    @PrimaryKey val id: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val updateAt: String
)

fun RecommendationEntity.asDomainModel(): Recommendation = Recommendation(
    description,
    dischargeDay,
    id,
    name,
    updateAt
)

fun List<RecommendationEntity>.asDomainModel(): List<Recommendation> = map { it.asDomainModel() }*/

@Entity(tableName = "recommendations_database")
data class UserRecommendationEntity (
    @PrimaryKey val operationDate: String,
    @ColumnInfo val patientId: Long
    //@ColumnInfo  val recommendationId: Long
)

fun UserRecommendationEntity.asDomainModel(): UserRecommendation = UserRecommendation(
    operationDate,
    patientId
    //recommendationId
)

fun List<UserRecommendationEntity>.asDomainModel(): List<UserRecommendation> = map { it.asDomainModel() }