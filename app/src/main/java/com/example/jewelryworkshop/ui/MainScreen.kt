package com.example.jewelryworkshop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jewelryworkshop.ui.MainViewModel
import com.jewelryworkshop.app.domain.model.Transaction
import com.example.jewelryworkshop.ui.components.BalanceCard
import com.example.jewelryworkshop.ui.components.TransactionItem

/**
 * Главный экран приложения
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Transaction) -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val metalBalance by viewModel.metalBalance.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Long?>(null) }

    // Диалог подтверждения удаления
    if (showDeleteDialog && transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                transactionToDelete = null
            },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы действительно хотите удалить эту операцию?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionToDelete?.let { viewModel.deleteTransaction(it) }
                        showDeleteDialog = false
                        transactionToDelete = null
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        transactionToDelete = null
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ювелирная мастерская") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить операцию",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Карточка с балансом металлов/изделий
                BalanceCard(balance = metalBalance)

                Spacer(modifier = Modifier.height(16.dp))

                // Заголовок списка операций
                Text(
                    text = "ОПЕРАЦИИ С МЕТАЛЛАМИ/ИЗДЕЛИЯМИ",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Список операций с металлами/изделиями
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет операций. Нажмите + чтобы добавить.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onEditClick = onNavigateToEditTransaction,
                        onDeleteClick = { id ->
                            transactionToDelete = id
                            showDeleteDialog = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Пространство внизу для FAB
                }
            }
        }
    }
}