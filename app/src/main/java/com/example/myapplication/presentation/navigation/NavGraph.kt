package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.repository.BudgetRepository
import com.example.myapplication.data.repository.BudgetItemRepository
import com.example.myapplication.data.repository.ClientRepository
import com.example.myapplication.presentation.ui.screen.AddItemScreen
import com.example.myapplication.presentation.ui.screen.BudgetDetailScreen
import com.example.myapplication.presentation.ui.screen.BudgetListScreen
import com.example.myapplication.presentation.ui.screen.CreateBudgetScreen
import com.example.myapplication.presentation.ui.screen.HomeScreen
import com.example.myapplication.presentation.viewmodel.AddItemViewModel
import com.example.myapplication.presentation.viewmodel.BudgetDetailViewModel
import com.example.myapplication.presentation.viewmodel.BudgetListViewModel
import com.example.myapplication.presentation.viewmodel.CreateBudgetViewModel
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
    clientRepository: ClientRepository,
    budgetItemRepository: BudgetItemRepository,
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
            val viewModel: BudgetListViewModel = viewModel(
                factory = BudgetListViewModel.provideFactory(budgetRepository)
            )
            BudgetListScreen(
                viewModel = viewModel,
                onNavigateToCreateBudget = {
                    navController.navigate(Destination.CreateBudget.route)
                },
                onNavigateToBudgetDetail = { budgetId ->
                    navController.navigate(Destination.BudgetDetail.createRoute(budgetId))
                },
                onNavigateToHome = {
                    navController.navigate(Destination.Home.route)
                }
            )
        }

        composable(Destination.CreateBudget.route) {
            val viewModel: CreateBudgetViewModel = viewModel(
                factory = CreateBudgetViewModel.provideFactory(budgetRepository, clientRepository)
            )
            CreateBudgetScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBudgetCreated = { budgetId ->
                    navController.navigate(Destination.BudgetDetail.createRoute(budgetId)) {
                        popUpTo(Destination.CreateBudget.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destination.BudgetDetail.route) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getString("budgetId")?.toIntOrNull() ?: 0
            val viewModel: BudgetDetailViewModel = viewModel(
                factory = BudgetDetailViewModel.provideFactory(budgetRepository, budgetItemRepository, budgetId)
            )
            BudgetDetailScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddItem = { bId ->
                    navController.navigate(Destination.AddItem.createRoute(bId))
                },
                onBudgetDeleted = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destination.AddItem.route) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getString("budgetId")?.toIntOrNull() ?: 0
            val viewModel: AddItemViewModel = viewModel(
                factory = AddItemViewModel.provideFactory(budgetItemRepository, budgetId)
            )
            AddItemScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onItemAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destination.Settings.route) {
            // TODO: Implement SettingsScreen (Fase 7)
            HomeScreen(
                viewModel = viewModel(
                    factory = HomeViewModel.provideFactory(budgetRepository)
                ),
                onNavigateToBudgetList = {},
                onNavigateToCreateBudget = {},
                onNavigateToSettings = {}
            )
        }
    }
}
