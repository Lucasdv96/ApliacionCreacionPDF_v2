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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.myapplication.presentation.viewmodel.BudgetDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(
    viewModel: BudgetDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddItem: (budgetId: Int) -> Unit,
    onBudgetDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }

    // Navegar si se eliminó
    if (uiState.budget == null && uiState.error?.contains("no encontrado") == true) {
        onBudgetDeleted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.budget?.budgetNumber ?: "Presupuesto")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.budget == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Presupuesto no encontrado")
                    Button(onClick = onNavigateBack, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Volver")
                    }
                }
            }

            else -> {
                val budget = uiState.budget!!
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sección Cliente
                    SectionTitle("👤 CLIENTE")
                    Text(text = "Nombre: ${budget.project}", fontSize = 14.sp)
                    Text(text = "Proyecto: ${budget.project}", fontSize = 14.sp)
                    Text(
                        text = "Creado: ${dateFormat.format(Date(budget.createdDate))}",
                        fontSize = 12.sp
                    )

                    // Sección Estado
                    SectionTitle("📋 ESTADO")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estado:", modifier = Modifier.weight(0.3f))
                        Button(
                            onClick = { showStatusMenu = true },
                            modifier = Modifier.weight(0.7f)
                        ) {
                            Text(budget.status)
                            DropdownMenu(
                                expanded = showStatusMenu,
                                onDismissRequest = { showStatusMenu = false }
                            ) {
                                listOf("DRAFT", "SENT", "ACCEPTED", "REJECTED", "COMPLETED")
                                    .forEach { status ->
                                        DropdownMenuItem(
                                            text = { Text(status) },
                                            onClick = {
                                                viewModel.updateBudgetStatus(status)
                                                showStatusMenu = false
                                            }
                                        )
                                    }
                            }
                        }
                    }

                    // Sección Items
                    SectionTitle("🛠️ ITEMS")
                    if (budget.laborCostPerItem > 0) {
                        Text(
                            text = "Mano de obra por item: \$${String.format("%.2f", budget.laborCostPerItem)}",
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = { onNavigateToAddItem(budget.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Agregar Item")
                    }

                    // Sección Resumen
                    SectionTitle("💰 RESUMEN")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Items:", fontWeight = FontWeight.Bold)
                        Text("\$0.00")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mano de obra:", fontWeight = FontWeight.Bold)
                        Text("\$0.00")
                    }
                    androidx.compose.material3.Divider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("\$0.00", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // Sección Notas
                    SectionTitle("📝 NOTAS")
                    OutlinedTextField(
                        value = budget.notes,
                        onValueChange = { viewModel.updateBudgetNotes(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        minLines = 3,
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
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            enabled = !uiState.isSaving
                        ) {
                            Text("Eliminar")
                        }

                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            enabled = !uiState.isSaving
                        ) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación
    if (showDeleteConfirm) {
        ConfirmDeleteDialog(
            onConfirm = {
                viewModel.deleteBudget()
                showDeleteConfirm = false
                onBudgetDeleted()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}
