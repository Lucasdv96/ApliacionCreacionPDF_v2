package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.repository.BudgetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder { DATE_DESC, DATE_ASC, STATUS }

private data class FilterState(
    val query: String = "",
    val status: String? = null,
    val sortOrder: SortOrder = SortOrder.DATE_DESC,
    val dateFrom: Long? = null,
    val dateTo: Long? = null
)

data class BudgetListUiState(
    val budgets: List<BudgetEntity> = emptyList(),
    val filteredBudgets: List<BudgetEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: String? = null,
    val sortOrder: SortOrder = SortOrder.DATE_DESC,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetListViewModel(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(FilterState())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val budgetsFlow = _filterState.flatMapLatest { filter ->
        if (filter.query.isBlank()) {
            budgetRepository.getAllBudgets()
        } else {
            budgetRepository.searchBudgetsWithClient(filter.query)
        }
    }

    val uiState: StateFlow<BudgetListUiState> = combine(
        budgetsFlow,
        _filterState,
        _isLoading,
        _error
    ) { budgets, filter, loading, error ->
        val filtered = budgets
            .filter { b ->
                val matchesStatus = filter.status == null || b.status == filter.status
                val matchesDateFrom = filter.dateFrom == null || b.createdDate >= filter.dateFrom
                val matchesDateTo = filter.dateTo == null || b.createdDate <= filter.dateTo
                matchesStatus && matchesDateFrom && matchesDateTo
            }
            .let { list ->
                when (filter.sortOrder) {
                    SortOrder.DATE_DESC -> list.sortedByDescending { it.createdDate }
                    SortOrder.DATE_ASC -> list.sortedBy { it.createdDate }
                    SortOrder.STATUS -> list.sortedBy { it.status }
                }
            }

        BudgetListUiState(
            budgets = budgets,
            filteredBudgets = filtered,
            searchQuery = filter.query,
            selectedStatus = filter.status,
            sortOrder = filter.sortOrder,
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            isLoading = loading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BudgetListUiState()
    )

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun setStatusFilter(status: String?) {
        _filterState.value = _filterState.value.copy(status = status)
    }

    fun setSortOrder(order: SortOrder) {
        _filterState.value = _filterState.value.copy(sortOrder = order)
    }

    fun setDateRange(from: Long?, to: Long?) {
        _filterState.value = _filterState.value.copy(dateFrom = from, dateTo = to)
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
