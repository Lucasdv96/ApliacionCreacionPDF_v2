package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BudgetItem(
    val id: Int = 0,
    val budgetId: Int = 0,
    val type: ItemType = ItemType.WINDOW,
    val description: String = "",
    val specifications: String = "", // JSON string with detailed specs
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val laborCost: Double = 0.0,
    val totalPrice: Double = 0.0,
    val notes: String = ""
) {
    fun getSubtotal(): Double = (unitPrice + laborCost) * quantity
}
