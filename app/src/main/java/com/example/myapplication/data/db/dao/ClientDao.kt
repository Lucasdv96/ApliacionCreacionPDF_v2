package com.example.myapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.db.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert
    suspend fun insert(client: ClientEntity): Long

    @Update
    suspend fun update(client: ClientEntity)

    @Delete
    suspend fun delete(client: ClientEntity)

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: Int): ClientEntity?

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE name LIKE :query ORDER BY name ASC")
    fun searchClients(query: String): Flow<List<ClientEntity>>

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteById(id: Int)
}
