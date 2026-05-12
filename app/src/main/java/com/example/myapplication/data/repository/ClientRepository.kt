package com.example.myapplication.data.repository

import com.example.myapplication.data.db.dao.ClientDao
import com.example.myapplication.data.db.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val clientDao: ClientDao) {

    fun getAllClients(): Flow<List<ClientEntity>> = clientDao.getAllClients()

    fun searchClients(query: String): Flow<List<ClientEntity>> =
        clientDao.searchClients("%$query%")

    suspend fun getClientById(id: Int): ClientEntity? = clientDao.getClientById(id)

    suspend fun createClient(client: ClientEntity): Long = clientDao.insert(client)

    suspend fun updateClient(client: ClientEntity) = clientDao.update(client)

    suspend fun deleteClient(client: ClientEntity) = clientDao.delete(client)

    suspend fun deleteClientById(id: Int) = clientDao.deleteById(id)
}
