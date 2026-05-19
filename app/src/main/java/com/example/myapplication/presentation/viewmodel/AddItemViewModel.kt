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
    val widthMm: Int = 0,
    val heightMm: Int = 0,
    val panelCount: Int = 0,
    val panelTypes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val itemSaved: Boolean = false,
    val isEditMode: Boolean = false
)

class AddItemViewModel(
    private val budgetItemRepository: BudgetItemRepository,
    private val budgetId: Int,
    private val itemId: Int? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()

    private var existingItem: BudgetItemEntity? = null

    init {
        if (itemId != null) {
            loadExistingItem(itemId)
        }
    }

    private fun loadExistingItem(id: Int) {
        viewModelScope.launch {
            try {
                val item = budgetItemRepository.getItemById(id)
                if (item != null) {
                    existingItem = item
                    _uiState.value = _uiState.value.copy(
                        type = item.type,
                        description = item.description,
                        specifications = item.specifications,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice,
                        laborCost = item.laborCost,
                        notes = item.notes,
                        widthMm = item.widthMm,
                        heightMm = item.heightMm,
                        panelCount = item.panelCount,
                        panelTypes = item.panelTypes,
                        isEditMode = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al cargar item: ${e.message}")
            }
        }
    }

    fun updateType(type: String) { _uiState.value = _uiState.value.copy(type = type) }
    fun updateDescription(description: String) { _uiState.value = _uiState.value.copy(description = description) }
    fun updateSpecifications(specifications: String) { _uiState.value = _uiState.value.copy(specifications = specifications) }
    fun updateQuantity(quantity: Int) { _uiState.value = _uiState.value.copy(quantity = if (quantity > 0) quantity else 1) }
    fun updateUnitPrice(price: Double) { _uiState.value = _uiState.value.copy(unitPrice = if (price >= 0) price else 0.0) }
    fun updateLaborCost(cost: Double) { _uiState.value = _uiState.value.copy(laborCost = if (cost >= 0) cost else 0.0) }
    fun updateNotes(notes: String) { _uiState.value = _uiState.value.copy(notes = notes) }
    fun updateWidthMm(value: Int) { _uiState.value = _uiState.value.copy(widthMm = if (value >= 0) value else 0) }
    fun updateHeightMm(value: Int) { _uiState.value = _uiState.value.copy(heightMm = if (value >= 0) value else 0) }
    fun updatePanelCount(value: Int) {
        val count = if (value >= 0) value else 0
        val types = List(count) { i ->
            _uiState.value.panelTypes.split(",").getOrNull(i) ?: "M"
        }.joinToString(",")
        _uiState.value = _uiState.value.copy(panelCount = count, panelTypes = types)
    }

    fun togglePanelType(index: Int) {
        val list = _uiState.value.panelTypes.split(",").toMutableList()
        while (list.size <= index) list.add("M")
        list[index] = if (list[index] == "F") "M" else "F"
        _uiState.value = _uiState.value.copy(panelTypes = list.joinToString(","))
    }

    fun saveItem() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true, error = null)
                val s = _uiState.value

                if (s.isEditMode && existingItem != null) {
                    val updated = existingItem!!.copy(
                        type = s.type,
                        description = s.description,
                        specifications = s.specifications,
                        quantity = s.quantity,
                        unitPrice = s.unitPrice,
                        laborCost = s.laborCost,
                        notes = s.notes,
                        widthMm = s.widthMm,
                        heightMm = s.heightMm,
                        panelCount = s.panelCount,
                        panelTypes = s.panelTypes
                    )
                    budgetItemRepository.updateItem(updated)
                } else {
                    val item = BudgetItemEntity(
                        budgetId = budgetId,
                        type = s.type,
                        description = s.description,
                        specifications = s.specifications,
                        quantity = s.quantity,
                        unitPrice = s.unitPrice,
                        laborCost = s.laborCost,
                        notes = s.notes,
                        widthMm = s.widthMm,
                        heightMm = s.heightMm,
                        panelCount = s.panelCount,
                        panelTypes = s.panelTypes
                    )
                    budgetItemRepository.createItem(item)
                }

                _uiState.value = _uiState.value.copy(isSaving = false, itemSaved = true, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "Error al guardar item: ${e.message}")
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    companion object {
        fun provideFactory(
            budgetItemRepository: BudgetItemRepository,
            budgetId: Int,
            itemId: Int? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AddItemViewModel(budgetItemRepository, budgetId, itemId) as T
        }
    }
}
