package ru.poas.patientassistant.client.db.recommendations

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecommendationsDao {

    @Query("select * from recommendations_database")
    fun getAllRecommendations(): LiveData<List<RecommendationEntity>>

    @Query("select * from recommendations_database where id = :id")
    fun getById(id: Long): LiveData<RecommendationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recommendations: List<RecommendationEntity>)

    @Query("delete from recommendations_database where id = :id")
    fun deleteById(id: Long)

    @Query("update recommendations_database set isConfirmed = 1 where recommendationUnitId = :recommendationUnitId")
    fun confirmRecommendationById(recommendationUnitId: Long)

    @Query("select isConfirmed from recommendations_database where recommendationUnitId = :recommendationUnitId")
    fun getIsRecommendationConfirmedById(recommendationUnitId: Long): Boolean

    @Query("delete from recommendations_database")
    fun clear()
}