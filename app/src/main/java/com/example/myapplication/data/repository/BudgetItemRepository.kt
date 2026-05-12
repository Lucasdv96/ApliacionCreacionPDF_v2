package com.example.myapplication.data.repository

import com.example.myapplication.data.db.dao.BudgetItemDao
import com.example.myapplication.data.db.entity.BudgetItemEntity
import kotlinx.coroutines.flow.Flow

class BudgetItemRepository(private val budgetItemDao: BudgetItemDao) {

    fun getItemsByBudget(budgetId: Int): Flow<List<BudgetItemEntity>> =
        budgetItemDao.getItemsByBudget(budgetId)

    suspend fun createItem(item: BudgetItemEntity): Long =
        budgetItemDao.insert(item)

    suspend fun updateItem(item: BudgetItemEntity) =
        budgetItemDao.update(item)

    suspend fun deleteItem(item: BudgetItemEntity) =
        budgetItemDao.delete(item)

    suspend fun deleteItemById(id: Int) =
        budgetItemDao.deleteById(id)

    suspend fun getItemById(id: Int): BudgetItemEntity? =
        budgetItemDao.getItemById(id)

    suspend fun deleteByBudgetId(budgetId: Int) =
        budgetItemDao.deleteByBudgetId(budgetId)
}
