package com.example.myapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.db.entity.BudgetItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetItemDao {
    @Insert
    suspend fun insert(item: BudgetItemEntity): Long

    @Update
    suspend fun update(item: BudgetItemEntity)

    @Delete
    suspend fun delete(item: BudgetItemEntity)

    @Query("SELECT * FROM budget_items WHERE id = :id")
    suspend fun getItemById(id: Int): BudgetItemEntity?

    @Query("SELECT * FROM budget_items WHERE budgetId = :budgetId ORDER BY id ASC")
    fun getItemsByBudget(budgetId: Int): Flow<List<BudgetItemEntity>>

    @Query("DELETE FROM budget_items WHERE budgetId = :budgetId")
    suspend fun deleteByBudgetId(budgetId: Int)

    @Query("DELETE FROM budget_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}
