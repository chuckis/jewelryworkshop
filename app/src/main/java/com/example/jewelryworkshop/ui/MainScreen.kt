package com.example.jewelryworkshop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.ui.components.TransactionItem

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactionDetail: (Transaction) -> Unit,
    onNavigateToAlloyManagement: () -> Unit // Добавляем новый параметр навигации
) {
    val transactions by viewModel.transactions.collectAsState()
    val metalBalance by viewModel.metalBalance.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    if (showDeleteDialog && transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                transactionToDelete = null
            },
            title = { Text(stringResource(R.string.deletion_confirmation)) },
            text = { Text(stringResource(R.string.are_u_sure_delete)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionToDelete?.let { viewModel.deleteTransaction(it) }
                        showDeleteDialog = false
                        transactionToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        transactionToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.all_transactions))},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Добавляем кнопку для перехода к управлению сплавами
                    IconButton(
                        onClick = onNavigateToAlloyManagement
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings, // или другую подходящую иконку
                            contentDescription = stringResource(R.string.alloy_management),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_transaction),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.theres_no_transactions),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onNavigateToTransactionDetail(transaction) },
                            onDeleteClick = { id ->
                                transactionToDelete = transaction
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    )
}