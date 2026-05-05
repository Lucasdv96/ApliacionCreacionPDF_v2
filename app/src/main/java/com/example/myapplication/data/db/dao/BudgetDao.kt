package com.example.myapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.db.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Int): BudgetEntity?

    @Query("SELECT * FROM budgets ORDER BY createdDate DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE clientId = :clientId ORDER BY createdDate DESC")
    fun getBudgetsByClient(clientId: Int): Flow<List<BudgetEntity>>

    @Query("SELECT COUNT(*) FROM budgets")
    suspend fun getBudgetCount(): Int

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Int)
}
