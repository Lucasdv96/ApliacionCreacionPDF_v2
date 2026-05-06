package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BudgetDetailUiState(
    val budget: BudgetEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

class BudgetDetailViewModel(
    private val budgetRepository: BudgetRepository,
    private val budgetId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<BudgetDetailUiState>(
        BudgetDetailUiState(isLoading = true)
    )
    val uiState: StateFlow<BudgetDetailUiState> = _uiState.asStateFlow()

    init {
        loadBudget()
    }

    private fun loadBudget() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val budget = budgetRepository.getBudgetById(budgetId)
                _uiState.value = _uiState.value.copy(
                    budget = budget,
                    isLoading = false,
                    error = if (budget == null) "Presupuesto no encontrado" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar presupuesto: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updateBudgetStatus(newStatus: String) {
        val currentBudget = _uiState.value.budget ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                val updatedBudget = currentBudget.copy(
                    status = newStatus,
                    modifiedDate = System.currentTimeMillis()
                )
                budgetRepository.updateBudget(updatedBudget)
                _uiState.value = _uiState.value.copy(
                    budget = updatedBudget,
                    isSaving = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar estado: ${e.message}",
                    isSaving = false
                )
            }
        }
    }

    fun updateBudgetNotes(notes: String) {
        val currentBudget = _uiState.value.budget ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                val updatedBudget = currentBudget.copy(
                    notes = notes,
                    modifiedDate = System.currentTimeMillis()
                )
                budgetRepository.updateBudget(updatedBudget)
                _uiState.value = _uiState.value.copy(
                    budget = updatedBudget,
                    isSaving = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al guardar notas: ${e.message}",
                    isSaving = false
                )
            }
        }
    }

    fun toggleEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditing = !_uiState.value.isEditing
        )
    }

    fun deleteBudget() {
        val currentBudget = _uiState.value.budget ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                budgetRepository.deleteBudgetById(currentBudget.id)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar: ${e.message}",
                    isSaving = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    companion object {
        fun provideFactory(
            budgetRepository: BudgetRepository,
            budgetId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BudgetDetailViewModel(budgetRepository, budgetId) as T
            }
        }
    }
}
