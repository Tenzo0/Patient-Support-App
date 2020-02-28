package ru.poas.patientassistant.client.db.recommendations

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecommendationsDao {

    @Query("select * from recommendations_database")
    fun getUserRecommendation(): LiveData<List<RecommendationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recommendations: List<RecommendationEntity>)

    @Query("delete from recommendations_database")
    fun clear()
}