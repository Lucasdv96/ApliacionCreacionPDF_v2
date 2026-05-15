package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.myapplication.utils.formatCurrency
import com.example.myapplication.utils.toInputString
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.viewmodel.AddItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel,
    onNavigateBack: () -> Unit,
    onItemAdded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showTypeMenu by remember { mutableStateOf(false) }

    if (uiState.itemSaved) {
        onItemAdded()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Editar Item" else "Agregar Item") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle("TIPO DE ITEM")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tipo:", modifier = Modifier.weight(0.3f))
                Button(
                    onClick = { showTypeMenu = true },
                    modifier = Modifier.weight(0.7f)
                ) {
                    val displayType = when (uiState.type) {
                        "WINDOW" -> "Ventana"
                        "DOOR" -> "Puerta"
                        "RAILING" -> "Baranda"
                        "LABOR" -> "Mano de obra"
                        else -> "Otro"
                    }
                    Text(displayType)
                    DropdownMenu(
                        expanded = showTypeMenu,
                        onDismissRequest = { showTypeMenu = false }
                    ) {
                        listOf("WINDOW", "DOOR", "RAILING", "LABOR", "OTHER").forEach { type ->
                            val displayName = when (type) {
                                "WINDOW" -> "Ventana"
                                "DOOR" -> "Puerta"
                                "RAILING" -> "Baranda"
                                "LABOR" -> "Mano de obra"
                                else -> "Otro"
                            }
                            DropdownMenuItem(
                                text = { Text(displayName) },
                                onClick = {
                                    viewModel.updateType(type)
                                    showTypeMenu = false
                                }
                            )
                        }
                    }
                }
            }

            SectionTitle("INFORMACIÓN DEL ITEM")

            FormTextField(
                label = "Descripción",
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Especificaciones",
                value = uiState.specifications,
                onValueChange = viewModel::updateSpecifications,
                enabled = !uiState.isSaving
            )

            SectionTitle("CÁLCULO DE PRECIO")

            FormTextField(
                label = "Cantidad",
                value = if (uiState.quantity == 0) "" else uiState.quantity.toString(),
                onValueChange = { value ->
                    val intValue = value.toIntOrNull() ?: 1
                    viewModel.updateQuantity(intValue)
                },
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Precio Unitario",
                value = uiState.unitPrice.toInputString(),
                onValueChange = { value ->
                    val doubleValue = value.toDoubleOrNull() ?: 0.0
                    viewModel.updateUnitPrice(doubleValue)
                },
                enabled = !uiState.isSaving
            )

            if (uiState.quantity > 0 && uiState.unitPrice > 0.0) {
                Text(
                    text = "Subtotal: ${formatCurrency(uiState.quantity * uiState.unitPrice)}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (uiState.type != "LABOR") {
                SectionTitle("MANO DE OBRA")
                FormTextField(
                    label = "Costo de Mano de Obra (opcional)",
                    value = uiState.laborCost.toInputString(),
                    onValueChange = { value ->
                        val doubleValue = value.toDoubleOrNull() ?: 0.0
                        viewModel.updateLaborCost(doubleValue)
                    },
                    enabled = !uiState.isSaving
                )
            }

            SectionTitle("NOTAS")

            FormTextField(
                label = "Notas",
                value = uiState.notes,
                onValueChange = viewModel::updateNotes,
                enabled = !uiState.isSaving
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
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
                    onClick = viewModel::saveItem,
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
                    Text(if (uiState.isEditMode) "Guardar Cambios" else "Guardar Item")
                }
            }
        }
    }
}
