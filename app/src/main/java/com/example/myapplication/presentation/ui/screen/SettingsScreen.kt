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
import androidx.compose.material3.OutlinedTextField
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
import com.example.myapplication.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
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
            SectionTitle("🏢 DATOS DE LA EMPRESA")

            FormTextField(
                label = "Nombre de la empresa",
                value = uiState.companyName,
                onValueChange = viewModel::updateCompanyName,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "CUIT",
                value = uiState.companyCuit,
                onValueChange = viewModel::updateCompanyCuit,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Dirección",
                value = uiState.companyAddress,
                onValueChange = viewModel::updateCompanyAddress,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Ciudad",
                value = uiState.companyCity,
                onValueChange = viewModel::updateCompanyCity,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Teléfono",
                value = uiState.companyPhone,
                onValueChange = viewModel::updateCompanyPhone,
                enabled = !uiState.isSaving
            )

            FormTextField(
                label = "Email",
                value = uiState.companyEmail,
                onValueChange = viewModel::updateCompanyEmail,
                enabled = !uiState.isSaving
            )

            SectionTitle("📄 TÉRMINOS Y CONDICIONES")

            Text(
                text = "Texto que aparecerá al pie del presupuesto PDF",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = uiState.termsConditions,
                onValueChange = viewModel::updateTermsConditions,
                label = { Text("Términos y condiciones") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                minLines = 4,
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
                    text = "Configuración guardada correctamente",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = viewModel::saveSettings,
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
                Text("Guardar configuración")
            }
        }
    }
}
