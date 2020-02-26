package ru.poas.patientassistant.client.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface RecommendationsDao {

    @Query("select * from recommendations_database")
    fun getAll(): LiveData<List<RecommendationEntity>>

    @Query("select * from recommendations_database where id = :id")
    fun getById(id: Long): LiveData<RecommendationEntity>

    @Query("delete from recommendations_database where id = :id")
    fun deleteById(id: Long)

    @Query("delete from recommendations_database")
    fun clear()
}