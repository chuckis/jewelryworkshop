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
import com.jewelryworkshop.ui.MainViewModel

/**
 * Основные навигационные маршруты в приложении
 */
object NavRoutes {
    const val MAIN = "main"
    const val ADD_TRANSACTION = "add_transaction"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
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
                onNavigateToEditTransaction = { transaction ->
                    navController.navigate("edit_transaction/${transaction.id}")
                }
            )
        }

        // Экран добавления новой транзакции
        composable(route = NavRoutes.ADD_TRANSACTION) {
            TransactionScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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

            TransactionScreen(
                viewModel = viewModel,
                existingTransaction = transaction,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}