package ru.poas.patientassistant.client.patient.db.drugs

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DrugsDao {
    @Query("select * from drugs_database")
    fun getAll(): LiveData<List<DrugEntity>>

    @Query("select * from drugs_database where id = :id")
    fun getById(id: Long): LiveData<DrugEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(drugs: List<DrugEntity>)

    @Query("delete from drugs_database where id = :id")
    fun deleteById(id: Long)

    @Query("delete from drugs_database")
    fun clear()
}