package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.viewmodel.EditClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientScreen(
    viewModel: EditClientViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Cliente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle("DATOS DEL CLIENTE")

            FormTextField(
                label = "Nombre *",
                value = uiState.name,
                onValueChange = viewModel::updateName,
                enabled = !uiState.isSaving
            )
            FormTextField(
                label = "CUIT",
                value = uiState.cuit,
                onValueChange = viewModel::updateCuit,
                enabled = !uiState.isSaving
            )
            FormTextField(
                label = "Dirección",
                value = uiState.address,
                onValueChange = viewModel::updateAddress,
                enabled = !uiState.isSaving
            )
            FormTextField(
                label = "Ciudad",
                value = uiState.city,
                onValueChange = viewModel::updateCity,
                enabled = !uiState.isSaving
            )
            FormTextField(
                label = "Provincia",
                value = uiState.province,
                onValueChange = viewModel::updateProvince,
                enabled = !uiState.isSaving
            )
            FormTextField(
                label = "Teléfono",
                value = uiState.phone,
                onValueChange = viewModel::updatePhone,
                enabled = !uiState.isSaving
            )
            FormTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                enabled = !uiState.isSaving
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            if (uiState.isSaved) {
                Text(
                    text = "Cliente guardado correctamente",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = viewModel::saveClient,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text("Guardar cambios")
            }
        }
    }
}
