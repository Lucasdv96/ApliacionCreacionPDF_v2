package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.db.entity.ClientEntity
import com.example.myapplication.data.repository.BudgetRepository
import com.example.myapplication.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateBudgetUiState(
    // Client data
    val clientName: String = "",
    val clientCuit: String = "",
    val clientAddress: String = "",
    val clientCity: String = "",
    val clientProvince: String = "",
    val clientPhone: String = "",
    val clientEmail: String = "",

    // Budget data
    val projectName: String = "",
    val laborCostPerItem: Double = 0.0,

    // UI state
    val isSaving: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val savedBudgetId: Int? = null
)

class CreateBudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBudgetUiState())
    val uiState: StateFlow<CreateBudgetUiState> = _uiState.asStateFlow()

    fun updateClientName(name: String) {
        _uiState.value = _uiState.value.copy(clientName = name)
    }

    fun updateClientCuit(cuit: String) {
        _uiState.value = _uiState.value.copy(clientCuit = cuit)
    }

    fun updateClientAddress(address: String) {
        _uiState.value = _uiState.value.copy(clientAddress = address)
    }

    fun updateClientCity(city: String) {
        _uiState.value = _uiState.value.copy(clientCity = city)
    }

    fun updateClientProvince(province: String) {
        _uiState.value = _uiState.value.copy(clientProvince = province)
    }

    fun updateClientPhone(phone: String) {
        _uiState.value = _uiState.value.copy(clientPhone = phone)
    }

    fun updateClientEmail(email: String) {
        _uiState.value = _uiState.value.copy(clientEmail = email)
    }

    fun updateProjectName(project: String) {
        _uiState.value = _uiState.value.copy(projectName = project)
    }

    fun updateLaborCostPerItem(cost: Double) {
        _uiState.value = _uiState.value.copy(laborCostPerItem = cost)
    }

    private fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        val currentState = _uiState.value

        if (currentState.clientName.isBlank()) {
            errors["clientName"] = "El nombre del cliente es requerido"
        }
        if (currentState.clientCity.isBlank()) {
            errors["clientCity"] = "La ciudad es requerida"
        }
        if (currentState.projectName.isBlank()) {
            errors["projectName"] = "El nombre del proyecto es requerido"
        }

        _uiState.value = _uiState.value.copy(validationErrors = errors)
        return errors.isEmpty()
    }

    fun saveBudget() {
        if (!validateForm()) {
            _uiState.value = _uiState.value.copy(
                error = "Por favor completa los campos requeridos"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true, error = null)

                val currentState = _uiState.value

                // Crear cliente
                val client = ClientEntity(
                    name = currentState.clientName,
                    cuit = currentState.clientCuit,
                    address = currentState.clientAddress,
                    city = currentState.clientCity,
                    province = currentState.clientProvince,
                    phone = currentState.clientPhone,
                    email = currentState.clientEmail
                )
                val clientIdLong = clientRepository.createClient(client)
                val clientId = if (clientIdLong > 0) clientIdLong.toInt() else 0

                // Crear presupuesto
                val budgetNumber = budgetRepository.generateBudgetNumber()
                val budget = BudgetEntity(
                    budgetNumber = budgetNumber,
                    clientId = clientId,
                    createdDate = System.currentTimeMillis(),
                    modifiedDate = System.currentTimeMillis(),
                    project = currentState.projectName,
                    laborCostPerItem = currentState.laborCostPerItem,
                    notes = "",
                    status = "DRAFT"
                )
                val budgetIdLong = budgetRepository.createBudget(budget)
                val budgetId = if (budgetIdLong > 0) budgetIdLong.toInt() else 0

                if (budgetId > 0) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        savedBudgetId = budgetId,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Error: No se pudo crear el presupuesto",
                        savedBudgetId = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error: ${e.localizedMessage ?: e.message}",
                    savedBudgetId = null
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
            clientRepository: ClientRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateBudgetViewModel(budgetRepository, clientRepository) as T
            }
        }
    }
}
