/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.alexey.patientassistant.client.patient.db.recommendations

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecommendationsDao {

    @Query("select * from recommendations_database")
    fun getAllRecommendations(): LiveData<List<RecommendationEntity>>

    @Query("select * from recommendations_database")
    fun getAll(): List<RecommendationEntity>

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

    @Query("select case when count(id) == 0 then cast (0 as BIT) else cast (1 as BIT) end from recommendations_database where day = :day")
    fun isRecommendationExist(day: Int): Boolean
}