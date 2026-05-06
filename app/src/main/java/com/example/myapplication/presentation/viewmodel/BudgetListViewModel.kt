package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BudgetListUiState(
    val budgets: List<BudgetEntity> = emptyList(),
    val filteredBudgets: List<BudgetEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class BudgetListViewModel(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Flujo reactivo que combina presupuestos con filtros
    val uiState: StateFlow<BudgetListUiState> = combine(
        budgetRepository.getAllBudgets(),
        _searchQuery,
        _selectedStatus,
        _isLoading,
        _error
    ) { budgets, query, status, loading, error ->
        val filtered = budgets.filter { budget ->
            val matchesQuery = query.isEmpty() ||
                budget.budgetNumber.contains(query, ignoreCase = true) ||
                budget.project.contains(query, ignoreCase = true)

            val matchesStatus = status == null || budget.status == status

            matchesQuery && matchesStatus
        }.sortedByDescending { it.createdDate }

        BudgetListUiState(
            budgets = budgets,
            filteredBudgets = filtered,
            searchQuery = query,
            selectedStatus = status,
            isLoading = loading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BudgetListUiState()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String?) {
        _selectedStatus.value = status
    }

    fun deleteBudget(budgetId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                budgetRepository.deleteBudgetById(budgetId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar presupuesto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        fun provideFactory(budgetRepository: BudgetRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BudgetListViewModel(budgetRepository) as T
                }
            }
    }
}
