package ru.poas.patientassistant.client.db.glossary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GlossaryDao {

    @Query("select * from glossary_database")
    fun getAll(): LiveData<List<GlossaryEntity>>

    @Query("select * from glossary_database where id = :id")
    fun getById(id: Long): LiveData<GlossaryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(glossaries: List<GlossaryEntity>)

    @Query("delete from glossary_database where id = :id")
    fun deleteById(id: Long)

    @Query("delete from glossary_database")
    fun clear()
}