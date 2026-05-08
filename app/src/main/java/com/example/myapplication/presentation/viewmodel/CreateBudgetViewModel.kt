package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.data.db.entity.ClientEntity
import com.example.myapplication.data.repository.BudgetRepository
import com.example.myapplication.data.repository.ClientRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CreateBudgetUiState(
    val clientName: String = "",
    val clientCuit: String = "",
    val clientAddress: String = "",
    val clientCity: String = "",
    val clientProvince: String = "",
    val clientPhone: String = "",
    val clientEmail: String = "",
    val projectName: String = "",
    val laborCost: Double = 0.0,
    val isExistingClientSelected: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val savedBudgetId: Int? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class CreateBudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBudgetUiState())
    val uiState: StateFlow<CreateBudgetUiState> = _uiState.asStateFlow()

    private val _selectedExistingClientId = MutableStateFlow<Int?>(null)
    private val _clientSearchQuery = MutableStateFlow("")

    val clientSuggestions: StateFlow<List<ClientEntity>> = _clientSearchQuery
        .flatMapLatest { query ->
            if (query.length >= 2 && _selectedExistingClientId.value == null) {
                clientRepository.searchClients(query)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateClientName(name: String) {
        _selectedExistingClientId.value = null
        _uiState.value = _uiState.value.copy(
            clientName = name,
            isExistingClientSelected = false
        )
        _clientSearchQuery.value = name
    }

    fun selectExistingClient(client: ClientEntity) {
        _selectedExistingClientId.value = client.id
        _clientSearchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            clientName = client.name,
            clientCuit = client.cuit,
            clientAddress = client.address,
            clientCity = client.city,
            clientProvince = client.province,
            clientPhone = client.phone,
            clientEmail = client.email,
            isExistingClientSelected = true
        )
    }

    fun clearClientSelection() {
        _selectedExistingClientId.value = null
        _uiState.value = _uiState.value.copy(isExistingClientSelected = false)
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

    fun updateLaborCost(cost: Double) {
        _uiState.value = _uiState.value.copy(laborCost = cost)
    }

    private fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()
        val s = _uiState.value
        if (s.clientName.isBlank()) errors["clientName"] = "El nombre del cliente es requerido"
        if (s.clientCity.isBlank()) errors["clientCity"] = "La ciudad es requerida"
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
                val s = _uiState.value
                val existingId = _selectedExistingClientId.value

                val clientId = if (existingId != null) {
                    val updated = ClientEntity(
                        id = existingId,
                        name = s.clientName,
                        cuit = s.clientCuit,
                        address = s.clientAddress,
                        city = s.clientCity,
                        province = s.clientProvince,
                        phone = s.clientPhone,
                        email = s.clientEmail
                    )
                    clientRepository.updateClient(updated)
                    existingId
                } else {
                    val client = ClientEntity(
                        name = s.clientName,
                        cuit = s.clientCuit,
                        address = s.clientAddress,
                        city = s.clientCity,
                        province = s.clientProvince,
                        phone = s.clientPhone,
                        email = s.clientEmail
                    )
                    clientRepository.createClient(client).toInt()
                }

                val budgetNumber = budgetRepository.generateBudgetNumber()
                val budget = BudgetEntity(
                    budgetNumber = budgetNumber,
                    clientId = clientId,
                    createdDate = System.currentTimeMillis(),
                    modifiedDate = System.currentTimeMillis(),
                    project = s.projectName,
                    laborCostPerItem = s.laborCost,
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
