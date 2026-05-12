package com.example.myapplication.data.repository

import com.example.myapplication.data.db.dao.BudgetDao
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.model.Budget
import com.example.myapplication.data.model.BudgetStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getAllBudgets(): Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    fun getBudgetsByClient(clientId: Int): Flow<List<BudgetEntity>> =
        budgetDao.getBudgetsByClient(clientId)

    suspend fun getBudgetById(id: Int): BudgetEntity? = budgetDao.getBudgetById(id)

    suspend fun createBudget(budget: BudgetEntity): Long = budgetDao.insert(budget)

    suspend fun updateBudget(budget: BudgetEntity) = budgetDao.update(budget)

    suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.delete(budget)

    suspend fun deleteBudgetById(id: Int) = budgetDao.deleteById(id)

    suspend fun getBudgetCount(): Int = budgetDao.getBudgetCount()

    fun searchBudgetsWithClient(query: String): Flow<List<BudgetEntity>> =
        budgetDao.searchBudgetsWithClient(query)

    suspend fun generateBudgetNumber(projectName: String): String {
        val count = getBudgetCount() + 1
        val monthFormat = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault())
        val month = monthFormat.format(java.util.Date())
        val sequence = count.toString().padStart(2, '0')
        return "$projectName-$month-$sequence"
    }
}