/*
 * Copyright (c) Alexey Barykin 2020.
 */

package ru.poas.patientassistant.client.patient.db.recommendations

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationConfirmKeyEntity
import ru.poas.patientassistant.client.patient.db.recommendations.RecommendationEntity

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConfirmation(recommendationConfirmKeyEntity: RecommendationConfirmKeyEntity)

    @Query("update recommendations_confirm_keys_db set isConfirmed = 1 where recommendationUnitId = :recommendationUnitId")
    fun confirmRecommendationById(recommendationUnitId: Long)

    @Query("select isConfirmed from recommendations_confirm_keys_db where recommendationUnitId = :recommendationUnitId")
    fun getIsRecommendationConfirmedById(recommendationUnitId: Long): Boolean

    @Query("delete from recommendations_database")
    fun clear()
}