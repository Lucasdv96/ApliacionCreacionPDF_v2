package com.example.myapplication.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val budgetNumber: String = "",
    val clientId: Int = 0,
    val createdDate: Long = 0L,
    val modifiedDate: Long = 0L,
    val project: String = "",
    val laborCostPerItem: Double = 0.0,
    val notes: String = "",
    val status: String = ""
)
