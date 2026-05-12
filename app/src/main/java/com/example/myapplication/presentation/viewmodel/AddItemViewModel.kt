package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.BudgetItemEntity
import com.example.myapplication.data.repository.BudgetItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddItemUiState(
    val type: String = "WINDOW",
    val description: String = "",
    val specifications: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val laborCost: Double = 0.0,
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val itemSaved: Boolean = false
)

class AddItemViewModel(
    private val budgetItemRepository: BudgetItemRepository,
    private val budgetId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()

    fun updateType(type: String) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateSpecifications(specifications: String) {
        _uiState.value = _uiState.value.copy(specifications = specifications)
    }

    fun updateQuantity(quantity: Int) {
        _uiState.value = _uiState.value.copy(quantity = if (quantity > 0) quantity else 1)
    }

    fun updateUnitPrice(price: Double) {
        _uiState.value = _uiState.value.copy(unitPrice = if (price >= 0) price else 0.0)
    }

    fun updateLaborCost(cost: Double) {
        _uiState.value = _uiState.value.copy(laborCost = if (cost >= 0) cost else 0.0)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun saveItem() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true, error = null)

                val currentState = _uiState.value
                val item = BudgetItemEntity(
                    budgetId = budgetId,
                    type = currentState.type,
                    description = currentState.description,
                    specifications = currentState.specifications,
                    quantity = currentState.quantity,
                    unitPrice = currentState.unitPrice,
                    laborCost = currentState.laborCost,
                    notes = currentState.notes
                )

                budgetItemRepository.createItem(item)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    itemSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error al guardar item: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    companion object {
        fun provideFactory(
            budgetItemRepository: BudgetItemRepository,
            budgetId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddItemViewModel(budgetItemRepository, budgetId) as T
            }
        }
    }
}
