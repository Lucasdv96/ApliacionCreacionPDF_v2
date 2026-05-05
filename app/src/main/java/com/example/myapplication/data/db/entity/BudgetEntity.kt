package com.example.myapplication.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val budgetNumber: String,
    val clientId: Int,
    val createdDate: Long,
    val modifiedDate: Long,
    val project: String,
    val laborCostPerItem: Double,
    val notes: String,
    val status: String
)
