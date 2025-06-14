package com.example.jewelryworkshop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.ui.components.TransactionItem

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactionDetail: (Transaction) -> Unit,
    onNavigateToAlloyManagement: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val transactionsByAlloy by viewModel.transactionsByAlloy.collectAsState()
    val alloysWithCounts by viewModel.alloysWithCounts.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    val allStr = stringResource(R.string.ALL)

    // Drawer state
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Состояние для TabRow и Pager
    val allTabs = remember(alloysWithCounts) {
        listOf(TabInfo(null, allStr, transactions.size)) +
                alloysWithCounts.map { alloyWithCount ->
                    TabInfo(
                        alloyId = alloyWithCount.alloy.id,
                        title = alloyWithCount.alloy.name,
                        count = alloyWithCount.transactionCount
                    )
                }
    }

    val pagerState = rememberPagerState(pageCount = { allTabs.size })

    // Диалог удаления
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

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onNavigateToAlloyManagement = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(NavRoutes.ALLOY_MANAGEMENT)
                },
                onNavigateToReportManagement = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(NavRoutes.REPORT_MANAGEMENT)
                },
//                onNavigateToShowBalance = {
//                    coroutineScope.launch {
//                        drawerState.close()
//                    }
//                    navController.navigate(NavRoutes.SHOW_BALANCE)
//                },
                onNavigateToAbout = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(NavRoutes.ABOUT)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.all_transactions)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Drawer",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (!isLoading) {
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
                }
            },
            content = { paddingValues ->
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.loading_transactions),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Вкладки
                        if (allTabs.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = pagerState.currentPage,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                edgePadding = 16.dp
                            ) {
                                allTabs.forEachIndexed { index, tab ->
                                    Tab(
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        },
                                        text = {
                                            Text(
                                                text = "${tab.title} (${tab.count})",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    )
                                }
                            }

                            HorizontalDivider()
                        }

                        // Контент вкладок
                        if (allTabs.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val currentTab = allTabs[page]
                                val filteredTransactions = when (currentTab.alloyId) {
                                    null -> transactions
                                    else -> transactionsByAlloy.entries
                                        .find { it.key.id == currentTab.alloyId }
                                        ?.value ?: emptyList()
                                }

                                TransactionsList(
                                    transactions = filteredTransactions,
                                    onTransactionClick = onNavigateToTransactionDetail,
                                    onDeleteClick = { transaction ->
                                        transactionToDelete = transaction
                                        showDeleteDialog = true
                                    },
                                    emptyMessage = if (currentTab.alloyId == null) {
                                        stringResource(R.string.theres_no_transactions)
                                    } else {
                                        "Нет транзакций для сплава ${currentTab.title}"
                                    }
                                )
                            }
                        } else {
                            TransactionsList(
                                transactions = emptyList(),
                                onTransactionClick = onNavigateToTransactionDetail,
                                onDeleteClick = { transaction ->
                                    transactionToDelete = transaction
                                    showDeleteDialog = true
                                },
                                emptyMessage = stringResource(R.string.theres_no_transactions)
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    onNavigateToAlloyManagement: () -> Unit,
    onNavigateToReportManagement: () -> Unit,
//    onNavigateToShowBalance: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )


            NavigationDrawerItem(
                label = {
                    Text(stringResource(R.string.alloy_management))
                },
                selected = false,
                onClick = onNavigateToAlloyManagement,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = {
                    Text(stringResource(R.string.report_management))
                },
                selected = false,
                onClick = onNavigateToReportManagement,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            //TODO()
//            NavigationDrawerItem(
//                icon = {
//                    Icon(
//                        imageVector = Icons.Default.AccountBalance,
//                        contentDescription = null
//                    )
//                },
//                label = {
//                    Text(stringResource(R.string.show_balance))
//                },
//                selected = false,
//                onClick = onNavigateToShowBalance,
//                modifier = Modifier.padding(vertical = 4.dp)
//            )

//            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(R.string.about))
                },
                selected = false,
                onClick = onNavigateToAbout,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit,
    onDeleteClick: (Transaction) -> Unit,
    emptyMessage: String
) {
    LazyColumn(
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = emptyMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.add_first_transaction),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) },
                    onDeleteClick = { onDeleteClick(transaction) }
                )
            }
        }
    }
}

private data class TabInfo(
    val alloyId: Long?,
    val title: String,
    val count: Int
)
