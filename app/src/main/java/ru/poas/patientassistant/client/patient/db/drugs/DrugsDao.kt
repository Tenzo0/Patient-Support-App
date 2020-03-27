package ru.poas.patientassistant.client.patient.db.drugs

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DrugsDao {
    @Query("select * from drugs_database")
    fun getAllLiveData(): LiveData<List<DrugEntity>>

    @Query("select * from drugs_database")
    fun getAll(): List<DrugEntity>

    @Query("select * from drugs_database where id = :id")
    fun getById(id: Long): LiveData<DrugEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(drugs: List<DrugEntity>)

    @Query("delete from drugs_database where id = :id")
    fun deleteById(id: Long)

    @Query("delete from drugs_database")
    fun clear()

    @Query("update drugs_database set realDateTimeOfMedicationReception = :date where id = :id")
    fun confirmDrugById(id: Long, date: String)

    @Query("delete from drugs_database where dateOfMedicationReception = :date")
    fun deleteByDate(date: String)
}