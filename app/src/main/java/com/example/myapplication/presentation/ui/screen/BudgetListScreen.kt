package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.presentation.ui.components.ConfirmDeleteDialog
import com.example.myapplication.presentation.viewmodel.BudgetListViewModel
import com.example.myapplication.presentation.viewmodel.SortOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(
    viewModel: BudgetListViewModel,
    onNavigateToCreateBudget: () -> Unit,
    onNavigateToBudgetDetail: (budgetId: Int) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf<Int?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Presupuestos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Text("←", fontSize = 20.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Filled.Sort, contentDescription = "Ordenar")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Más reciente primero",
                                    fontWeight = if (uiState.sortOrder == SortOrder.DATE_DESC) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                viewModel.setSortOrder(SortOrder.DATE_DESC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Más antiguo primero",
                                    fontWeight = if (uiState.sortOrder == SortOrder.DATE_ASC) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                viewModel.setSortOrder(SortOrder.DATE_ASC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Por estado",
                                    fontWeight = if (uiState.sortOrder == SortOrder.STATUS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                viewModel.setSortOrder(SortOrder.STATUS)
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateBudget) {
                Icon(Icons.Filled.Add, contentDescription = "Crear presupuesto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Búsqueda
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                label = { Text("Buscar por número, proyecto o cliente...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filtro por estado (scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilterButton("Todos", null, uiState.selectedStatus, viewModel::setStatusFilter)
                StatusFilterButton("Borrador", "DRAFT", uiState.selectedStatus, viewModel::setStatusFilter)
                StatusFilterButton("Enviado", "SENT", uiState.selectedStatus, viewModel::setStatusFilter)
                StatusFilterButton("Aceptado", "ACCEPTED", uiState.selectedStatus, viewModel::setStatusFilter)
                StatusFilterButton("Rechazado", "REJECTED", uiState.selectedStatus, viewModel::setStatusFilter)
                StatusFilterButton("Completado", "COMPLETED", uiState.selectedStatus, viewModel::setStatusFilter)
            }

            // Filtro por fecha
            DateFilterRow(
                dateFrom = uiState.dateFrom,
                onDateRangeSelected = { from, to -> viewModel.setDateRange(from, to) }
            )

            // Contador
            if (uiState.filteredBudgets.isNotEmpty()) {
                Text(
                    text = "${uiState.filteredBudgets.size} presupuesto(s)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Lista de presupuestos
            if (uiState.filteredBudgets.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (uiState.budgets.isEmpty()) "No hay presupuestos" else "Sin resultados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (uiState.budgets.isEmpty()) "Crea tu primer presupuesto" else "Probá con otros filtros",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredBudgets) { budget ->
                        BudgetItemCard(
                            budget = budget,
                            onViewClick = { onNavigateToBudgetDetail(budget.id) },
                            onDeleteClick = { showDeleteConfirm = budget.id }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirm != null) {
        ConfirmDeleteDialog(
            onConfirm = {
                viewModel.deleteBudget(showDeleteConfirm!!)
                showDeleteConfirm = null
            },
            onDismiss = { showDeleteConfirm = null }
        )
    }
}

@Composable
private fun StatusFilterButton(
    label: String,
    status: String?,
    selectedStatus: String?,
    onClick: (String?) -> Unit
) {
    val isSelected = selectedStatus == status
    if (isSelected) {
        Button(onClick = { onClick(status) }) {
            Text(label, fontSize = 12.sp)
        }
    } else {
        OutlinedButton(onClick = { onClick(status) }) {
            Text(label, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DateFilterRow(
    dateFrom: Long?,
    onDateRangeSelected: (Long?, Long?) -> Unit
) {
    val now = System.currentTimeMillis()

    val startOfMonth = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val thirtyDaysAgo = remember { now - 30L * 24 * 60 * 60 * 1000 }

    val startOfYear = remember {
        Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DateChip("Todas", dateFrom == null) { onDateRangeSelected(null, null) }
        DateChip("Este mes", dateFrom == startOfMonth) { onDateRangeSelected(startOfMonth, null) }
        DateChip("30 días", dateFrom == thirtyDaysAgo) { onDateRangeSelected(thirtyDaysAgo, null) }
        DateChip("Este año", dateFrom == startOfYear) { onDateRangeSelected(startOfYear, null) }
    }
}

@Composable
private fun DateChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    if (isSelected) {
        Button(onClick = onClick) {
            Text(label, fontSize = 11.sp)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label, fontSize = 11.sp)
        }
    }
}

@Composable
fun BudgetItemCard(
    budget: BudgetEntity,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.budgetNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = budget.project,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = dateFormat.format(Date(budget.createdDate)),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Text(
                    text = budget.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onViewClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Ver", fontSize = 12.sp)
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
