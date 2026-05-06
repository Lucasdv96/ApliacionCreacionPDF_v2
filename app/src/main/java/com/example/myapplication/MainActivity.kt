package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.di.AppDataContainer
import com.example.myapplication.presentation.navigation.AppNavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppDataContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar el contenedor una sola vez
        appContainer = AppDataContainer(this)

        setContent {
            MyApplicationTheme {
                AppNavGraph(
                    budgetRepository = appContainer.budgetRepository,
                    clientRepository = appContainer.clientRepository
                )
            }
        }
    }
}