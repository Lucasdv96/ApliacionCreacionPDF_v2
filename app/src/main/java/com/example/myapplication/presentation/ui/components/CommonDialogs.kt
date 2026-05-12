package com.example.myapplication.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Presupuesto") },
        text = { Text("¿Estás seguro de que deseas eliminar este presupuesto?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ShareOptionsDialog(
    onWhatsApp: () -> Unit,
    onEmail: () -> Unit,
    onGeneral: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Compartir Presupuesto") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Button(
                    onClick = onWhatsApp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📱  WhatsApp")
                }
                Button(
                    onClick = onEmail,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✉️  Email")
                }
                OutlinedButton(
                    onClick = onGeneral,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Otras apps...")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
