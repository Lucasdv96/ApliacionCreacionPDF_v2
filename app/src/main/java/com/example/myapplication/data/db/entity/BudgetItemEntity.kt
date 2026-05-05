package com.example.myapplication.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "budget_items",
    foreignKeys = [
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["id"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BudgetItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val budgetId: Int,
    val type: String,
    val description: String,
    val specifications: String,
    val quantity: Int,
    val unitPrice: Double,
    val laborCost: Double,
    val notes: String
)
