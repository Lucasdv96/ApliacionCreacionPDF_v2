package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myapplication.di.AppDataContainer
import com.example.myapplication.presentation.navigation.AppNavGraph
import com.example.myapplication.presentation.ui.screen.SplashScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppDataContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appContainer = AppDataContainer(this)

        setContent {
            MyApplicationTheme {
                var splashDone by remember { mutableStateOf(false) }

                if (!splashDone) {
                    SplashScreen(onFinished = { splashDone = true })
                } else {
                    AppNavGraph(
                        budgetRepository = appContainer.budgetRepository,
                        clientRepository = appContainer.clientRepository,
                        budgetItemRepository = appContainer.budgetItemRepository,
                        settingsRepository = appContainer.settingsRepository,
                        pdfGeneratorService = appContainer.pdfGeneratorService,
                        sharingService = appContainer.sharingService
                    )
                }
            }
        }
    }
}