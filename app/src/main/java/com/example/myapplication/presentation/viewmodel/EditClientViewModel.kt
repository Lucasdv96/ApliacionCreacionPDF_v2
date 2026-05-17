package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.entity.ClientEntity
import com.example.myapplication.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditClientUiState(
    val name: String = "",
    val cuit: String = "",
    val address: String = "",
    val city: String = "",
    val province: String = "",
    val phone: String = "",
    val email: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class EditClientViewModel(
    private val clientRepository: ClientRepository,
    private val clientId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditClientUiState())
    val uiState: StateFlow<EditClientUiState> = _uiState.asStateFlow()

    private var existingClient: ClientEntity? = null

    init {
        loadClient()
    }

    private fun loadClient() {
        viewModelScope.launch {
            try {
                val client = clientRepository.getClientById(clientId)
                if (client != null) {
                    existingClient = client
                    _uiState.value = _uiState.value.copy(
                        name = client.name,
                        cuit = client.cuit,
                        address = client.address,
                        city = client.city,
                        province = client.province,
                        phone = client.phone,
                        email = client.email
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al cargar cliente: ${e.message}")
            }
        }
    }

    fun updateName(value: String) { _uiState.value = _uiState.value.copy(name = value, isSaved = false) }
    fun updateCuit(value: String) { _uiState.value = _uiState.value.copy(cuit = value, isSaved = false) }
    fun updateAddress(value: String) { _uiState.value = _uiState.value.copy(address = value, isSaved = false) }
    fun updateCity(value: String) { _uiState.value = _uiState.value.copy(city = value, isSaved = false) }
    fun updateProvince(value: String) { _uiState.value = _uiState.value.copy(province = value, isSaved = false) }
    fun updatePhone(value: String) { _uiState.value = _uiState.value.copy(phone = value, isSaved = false) }
    fun updateEmail(value: String) { _uiState.value = _uiState.value.copy(email = value, isSaved = false) }

    fun saveClient() {
        val s = _uiState.value
        if (s.name.isBlank()) {
            _uiState.value = s.copy(error = "El nombre es requerido")
            return
        }
        viewModelScope.launch {
            try {
                _uiState.value = s.copy(isSaving = true, error = null)
                val updated = (existingClient ?: ClientEntity()).copy(
                    id = clientId,
                    name = s.name,
                    cuit = s.cuit,
                    address = s.address,
                    city = s.city,
                    province = s.province,
                    phone = s.phone,
                    email = s.email
                )
                clientRepository.updateClient(updated)
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "Error al guardar: ${e.message}")
            }
        }
    }

    companion object {
        fun provideFactory(clientRepository: ClientRepository, clientId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    EditClientViewModel(clientRepository, clientId) as T
            }
    }
}
