package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.viewmodel.CreateBudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetScreen(
    viewModel: CreateBudgetViewModel,
    onNavigateBack: () -> Unit,
    onBudgetCreated: (budgetId: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Navegar cuando se crea el presupuesto
    if (uiState.savedBudgetId != null) {
        onBudgetCreated(uiState.savedBudgetId!!)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Presupuesto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Cliente
            SectionTitle("DATOS DEL CLIENTE")

            FormTextField(
                label = "Nombre del Cliente *",
                value = uiState.clientName,
                onValueChange = viewModel::updateClientName,
                error = uiState.validationErrors["clientName"],
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "RUT",
                value = uiState.clientRut,
                onValueChange = viewModel::updateClientRut,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Dirección",
                value = uiState.clientAddress,
                onValueChange = viewModel::updateClientAddress,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Ciudad *",
                value = uiState.clientCity,
                onValueChange = viewModel::updateClientCity,
                error = uiState.validationErrors["clientCity"],
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Comuna",
                value = uiState.clientCommune,
                onValueChange = viewModel::updateClientCommune,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Teléfono",
                value = uiState.clientPhone,
                onValueChange = viewModel::updateClientPhone,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Email",
                value = uiState.clientEmail,
                onValueChange = viewModel::updateClientEmail,
                enabled = !uiState.isSaving
            )

            // Sección Presupuesto
            SectionTitle("PROYECTO")

            FormTextField(
                label = "Nombre del Proyecto *",
                value = uiState.projectName,
                onValueChange = viewModel::updateProjectName,
                error = uiState.validationErrors["projectName"],
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Costo de Mano de Obra por Item",
                value = if (uiState.laborCostPerItem == 0.0) "" else uiState.laborCostPerItem.toString(),
                onValueChange = { value ->
                    val doubleValue = value.toDoubleOrNull() ?: 0.0
                    viewModel.updateLaborCostPerItem(doubleValue)
                },
                enabled = !uiState.isSaving
            )

            // Mensaje de error
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    enabled = !uiState.isSaving
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = viewModel::saveBudget,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            label = { Text(label) },
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = error != null,
            singleLine = true
        )
        if (error != null) {
            Text(
                text = error,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
