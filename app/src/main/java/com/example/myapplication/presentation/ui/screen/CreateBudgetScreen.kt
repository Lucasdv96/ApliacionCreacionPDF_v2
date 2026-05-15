package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val clientSuggestions by viewModel.clientSuggestions.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showClientDropdown by remember { mutableStateOf(false) }

    if (uiState.savedBudgetId != null) {
        onBudgetCreated(uiState.savedBudgetId!!)
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
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
            SectionTitle("DATOS DEL CLIENTE")

            Column(modifier = Modifier.fillMaxWidth()) {
                FormTextField(
                    label = "Nombre del Cliente *",
                    value = uiState.clientName,
                    onValueChange = {
                        viewModel.updateClientName(it)
                        showClientDropdown = true
                    },
                    error = uiState.validationErrors["clientName"],
                    enabled = !uiState.isSaving
                )
                if (showClientDropdown && clientSuggestions.isNotEmpty() && !uiState.isExistingClientSelected) {
                    androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        clientSuggestions.take(5).forEach { client ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(client.name, fontWeight = FontWeight.Medium)
                                        val location = listOf(client.city, client.province)
                                            .filter { it.isNotBlank() }
                                            .joinToString(", ")
                                        if (location.isNotBlank()) {
                                            Text(location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.selectExistingClient(client)
                                    showClientDropdown = false
                                }
                            )
                            androidx.compose.material3.Divider()
                        }
                    }
                }
            }

            if (uiState.isExistingClientSelected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        "Cliente existente — datos precargados",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = viewModel::clearClientSelection) {
                        Text("Cambiar", fontSize = 12.sp)
                    }
                }
            }

            FormTextField(
                label = "CUIT",
                value = uiState.clientCuit,
                onValueChange = viewModel::updateClientCuit,
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
                label = "Provincia",
                value = uiState.clientProvince,
                onValueChange = viewModel::updateClientProvince,
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

            SectionTitle("PROYECTO")

            FormTextField(
                label = "Nombre del Proyecto *",
                value = uiState.projectName,
                onValueChange = viewModel::updateProjectName,
                error = uiState.validationErrors["projectName"],
                enabled = !uiState.isSaving
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

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
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
