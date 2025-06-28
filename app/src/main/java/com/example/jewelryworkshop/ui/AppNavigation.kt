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
import com.example.jewelryworkshop.domain.MetalAlloy


object NavRoutes {
    const val MAIN = "main"
    const val ADD_TRANSACTION = "add_transaction"
    const val TRANSACTION_DETAIL = "transaction_detail/{transactionId}"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
    const val ADD_ALLOY = "add_alloy"
    const val ALLOY_MANAGEMENT = "alloy_management"
    const val EDIT_ALLOY = "edit_alloy/{alloyId}"
    const val REPORT_MANAGEMENT = "report_management"
//    const val SHOW_BALANCE = "show_balance"
    const val ABOUT = "about"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    reportManagementViewModel: ReportManagementViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.MAIN
    ) {
        composable(route = NavRoutes.MAIN) {
            MainScreen(
                viewModel = viewModel,
                navController = navController,
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

        composable(route = NavRoutes.ADD_TRANSACTION) {
            TransactionAddScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

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
                navController.popBackStack()
            }
        }

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
                navController.popBackStack()
            }
        }

        composable(route = NavRoutes.ALLOY_MANAGEMENT) {
            AlloyManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
                onNavigateToAddAlloy = {
                    navController.navigate(NavRoutes.ADD_ALLOY)
                },
                onNavigateToEditAlloy = { alloy: MetalAlloy ->
                    navController.navigate("edit_alloy/${alloy.id}")
                }
            )
        }

        composable(route = NavRoutes.ADD_ALLOY) {
            AlloyAddScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NavRoutes.EDIT_ALLOY,
            arguments = listOf(
                navArgument("alloyId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val alloyId = backStackEntry.arguments?.getLong("alloyId") ?: 0
            val alloys = viewModel.alloys.value
            val alloy = alloys.find { it.id == alloyId }

            alloy?.let {
                AlloyEditScreen(
                    alloy = it,
                    mainViewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } ?: run {
                navController.popBackStack()
            }
        }
        composable(route = NavRoutes.REPORT_MANAGEMENT) {
            ReportManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = reportManagementViewModel
            )
        }

        composable(route = NavRoutes.ABOUT) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}