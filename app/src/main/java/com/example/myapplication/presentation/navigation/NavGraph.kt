package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.repository.BudgetRepository
import com.example.myapplication.presentation.ui.screen.HomeScreen
import com.example.myapplication.presentation.viewmodel.HomeViewModel

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object BudgetList : Destination("budget_list")
    data object CreateBudget : Destination("create_budget")
    data object BudgetDetail : Destination("budget_detail/{budgetId}") {
        fun createRoute(budgetId: Int) = "budget_detail/$budgetId"
    }
    data object AddItem : Destination("add_item/{budgetId}") {
        fun createRoute(budgetId: Int) = "add_item/$budgetId"
    }
    data object Settings : Destination("settings")
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    budgetRepository: BudgetRepository,
    startDestination: String = Destination.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(budgetRepository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToBudgetList = {
                    navController.navigate(Destination.BudgetList.route)
                },
                onNavigateToCreateBudget = {
                    navController.navigate(Destination.CreateBudget.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Destination.Settings.route)
                }
            )
        }

        composable(Destination.BudgetList.route) {
            // TODO: Implement BudgetListScreen
            HomeScreen(
                viewModel = viewModel(
                    factory = HomeViewModel.provideFactory(budgetRepository)
                ),
                onNavigateToBudgetList = {},
                onNavigateToCreateBudget = {
                    navController.navigate(Destination.CreateBudget.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Destination.Settings.route)
                }
            )
        }

        composable(Destination.CreateBudget.route) {
            // TODO: Implement CreateBudgetScreen
        }

        composable(Destination.Settings.route) {
            // TODO: Implement SettingsScreen
        }
    }
}
