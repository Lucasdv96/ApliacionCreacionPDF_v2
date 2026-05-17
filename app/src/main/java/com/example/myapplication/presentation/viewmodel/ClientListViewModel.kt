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

data class ClientListUiState(
    val clients: List<ClientEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ClientListViewModel(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientListUiState(isLoading = true))
    val uiState: StateFlow<ClientListUiState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    private fun loadClients() {
        viewModelScope.launch {
            try {
                clientRepository.getAllClients().collect { clients ->
                    _uiState.value = _uiState.value.copy(clients = clients, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar clientes: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            try {
                clientRepository.deleteClient(client)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al eliminar cliente: ${e.message}")
            }
        }
    }

    companion object {
        fun provideFactory(clientRepository: ClientRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ClientListViewModel(clientRepository) as T
            }
    }
}
