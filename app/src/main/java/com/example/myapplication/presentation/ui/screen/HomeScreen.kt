package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.db.entity.BudgetEntity
import com.example.myapplication.presentation.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToBudgetList: () -> Unit,
    onNavigateToCreateBudget: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val budgets by viewModel.budgets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MB Cerramientos") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
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
        if (budgets.isEmpty()) {
            EmptyBudgetsView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onCreateBudget = onNavigateToCreateBudget
            )
        } else {
            BudgetListView(
                budgets = budgets,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
fun EmptyBudgetsView(
    modifier: Modifier = Modifier,
    onCreateBudget: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tienes presupuestos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Crea tu primer presupuesto",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onCreateBudget,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Crear Presupuesto")
        }
    }
}

@Composable
fun BudgetListView(
    budgets: List<BudgetEntity>,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(budgets) { budget ->
            BudgetCard(budget = budget, dateFormat = dateFormat)
        }
    }
}

@Composable
fun BudgetCard(
    budget: BudgetEntity,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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
            Text(
                text = "Estado: ${budget.status}",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
