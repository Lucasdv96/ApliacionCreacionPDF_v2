package com.example.myapplication.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Budget(
    val id: Int = 0,
    val budgetNumber: String = "",
    val client: Client = Client(),
    val createdDate: Long = System.currentTimeMillis(),
    val modifiedDate: Long = System.currentTimeMillis(),
    val project: String = "",
    val items: List<BudgetItem> = emptyList(),
    val laborCostPerItem: Double = 0.0,
    val notes: String = "",
    val status: BudgetStatus = BudgetStatus.DRAFT
) {
    fun getTotal(): Double = items.sumOf { it.getSubtotal() }

    fun getTotalLabor(): Double = items.size * laborCostPerItem

    fun getGrandTotal(): Double = getTotal() + getTotalLabor()
}

@Serializable
enum class BudgetStatus {
    DRAFT, SENT, ACCEPTED, REJECTED, COMPLETED
}
