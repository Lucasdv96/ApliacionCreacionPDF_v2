package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.db.entity.BudgetItemEntity
import com.example.myapplication.data.repository.BudgetRepository
import com.example.myapplication.data.repository.BudgetItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class BudgetDetailUiState(
    val budget: BudgetEntity? = null,
    val items: List<BudgetItemEntity> = emptyList(),
    val client: com.example.myapplication.data.db.entity.ClientEntity? = null,
    val settings: com.example.myapplication.data.db.entity.SettingsEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isGeneratingPdf: Boolean = false,
    val pdfPath: String? = null
)

class BudgetDetailViewModel(
    private val budgetRepository: BudgetRepository,
    private val budgetItemRepository: BudgetItemRepository,
    private val clientRepository: com.example.myapplication.data.repository.ClientRepository,
    private val settingsRepository: com.example.myapplication.data.repository.SettingsRepository,
    private val pdfGeneratorService: com.example.myapplication.data.service.PdfGeneratorService,
    private val budgetId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<BudgetDetailUiState>(
        BudgetDetailUiState(isLoading = true)
    )
    val uiState: StateFlow<BudgetDetailUiState> = _uiState.asStateFlow()

    init {
        loadBudget()
        loadItems()
        loadSettings()
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

    private fun loadItems() {
        viewModelScope.launch {
            try {
                budgetItemRepository.getItemsByBudget(budgetId).collect { items ->
                    _uiState.value = _uiState.value.copy(items = items)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar items: ${e.message}"
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

    fun deleteItem(item: BudgetItemEntity) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                budgetItemRepository.deleteItem(item)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar item: ${e.message}",
                    isSaving = false
                )
            }
        }
    }

    fun getItemsTotal(): Double {
        return _uiState.value.items.sumOf { it.quantity * it.unitPrice }
    }

    fun getLaborTotal(): Double {
        return _uiState.value.items.sumOf { it.laborCost }
    }

    fun getGrandTotal(): Double {
        return getItemsTotal() + getLaborTotal() + (_uiState.value.budget?.laborCostPerItem ?: 0.0)
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                settingsRepository.getSettings().collect { settings ->
                    _uiState.value = _uiState.value.copy(settings = settings)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar configuración: ${e.message}"
                )
            }
        }
    }

    fun generatePdf() {
        val budget = _uiState.value.budget ?: return
        val items = _uiState.value.items
        val settings = _uiState.value.settings ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isGeneratingPdf = true, error = null)

                val client = clientRepository.getClientById(budget.clientId)
                val pdfPath = pdfGeneratorService.generateBudgetPdf(budget, items, client, settings)

                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    pdfPath = pdfPath,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    error = "Error al generar PDF: ${e.message}"
                )
            }
        }
    }

    fun clearPdfPath() {
        _uiState.value = _uiState.value.copy(pdfPath = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    companion object {
        fun provideFactory(
            budgetRepository: BudgetRepository,
            budgetItemRepository: BudgetItemRepository,
            clientRepository: com.example.myapplication.data.repository.ClientRepository,
            settingsRepository: com.example.myapplication.data.repository.SettingsRepository,
            pdfGeneratorService: com.example.myapplication.data.service.PdfGeneratorService,
            budgetId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BudgetDetailViewModel(
                    budgetRepository,
                    budgetItemRepository,
                    clientRepository,
                    settingsRepository,
                    pdfGeneratorService,
                    budgetId
                ) as T
            }
        }
    }
}
