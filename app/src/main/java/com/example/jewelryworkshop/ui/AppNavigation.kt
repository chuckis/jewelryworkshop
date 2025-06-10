package com.example.jewelryworkshop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * Основные навигационные маршруты в приложении
 */
object NavRoutes {
    const val MAIN = "main"
    const val ADD_TRANSACTION = "add_transaction"
    const val TRANSACTION_DETAIL = "transaction_detail/{transactionId}"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
    const val ALLOY_MANAGEMENT = "alloy_management"
    const val ADD_ALLOY = "add_alloy" // Добавляем маршрут для добавления сплава
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.MAIN
    ) {
        // Главный экран со списком операций и балансом
        composable(route = NavRoutes.MAIN) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToAddTransaction = {
                    navController.navigate(NavRoutes.ADD_TRANSACTION)
                },
                onNavigateToTransactionDetail = { transaction ->
                    navController.navigate("transaction_detail/${transaction.id}")
                },
                onNavigateToAlloyManagement = {
                    navController.navigate(NavRoutes.ALLOY_MANAGEMENT)
                }
            )
        }

        // Экран добавления новой транзакции
        composable(route = NavRoutes.ADD_TRANSACTION) {
            TransactionAddScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Экран просмотра деталей транзакции
        composable(
            route = NavRoutes.TRANSACTION_DETAIL,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0
            val transactions = viewModel.transactions.value
            val transaction = transactions.find { it.id == transactionId }

            transaction?.let {
                TransactionDetailScreen(
                    transaction = it,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEdit = {
                        navController.navigate("edit_transaction/${it.id}")
                    }
                )
            } ?: run {
                // Если транзакция не найдена, возвращаемся назад
                navController.popBackStack()
            }
        }

        // Экран редактирования существующей транзакции
        composable(
            route = NavRoutes.EDIT_TRANSACTION,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0
            val transactions = viewModel.transactions.value
            val transaction = transactions.find { it.id == transactionId }

            transaction?.let {
                TransactionEditScreen(
                    viewModel = viewModel,
                    existingTransaction = it,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } ?: run {
                // Если транзакция не найдена, возвращаемся назад
                navController.popBackStack()
            }
        }

        // Экран управления сплавами
        composable(route = NavRoutes.ALLOY_MANAGEMENT) {
            AlloyManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
                onNavigateToAddAlloy = {
                    navController.navigate(NavRoutes.ADD_ALLOY)
                }
            )
        }

        // Экран добавления нового сплава
        composable(route = NavRoutes.ADD_ALLOY) {
            AlloyAddScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}