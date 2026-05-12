package com.example.myapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.db.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert
    suspend fun insert(settings: SettingsEntity)

    @Update
    suspend fun update(settings: SettingsEntity)

    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettingsSync(): SettingsEntity?
}
