package com.example.jewelryworkshop.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.ui.components.TransactionItem
import com.example.jewelryworkshop.ui.components.SingleAlloyBalanceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavHostController,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAddAlloy: () -> Unit,
    onNavigateToTransactionDetail: (Transaction) -> Unit,
) {
    val transactions by viewModel.transactions.collectAsState()
    val transactionsByAlloy by viewModel.transactionsByAlloy.collectAsState()
    val alloys by viewModel.alloys.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    val allStr = stringResource(R.string.ALL)

    // Drawer state
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val allTabs = remember(transactions, alloys, transactionsByAlloy) {
        val allTab = TabInfo(null, allStr, transactions.size)

        val alloyTabs = alloys.map { alloy ->
            val transactionCount = transactionsByAlloy.entries
                .find { it.key.id == alloy.id }
                ?.value?.size ?: 0

            TabInfo(
                alloyId = alloy.id,
                title = alloy.name,
                count = transactionCount
            )
        }

        listOf(allTab) + alloyTabs
    }

    val pagerState = rememberPagerState(pageCount = { allTabs.size })

    LaunchedEffect(allTabs.size) {
        if (pagerState.currentPage >= allTabs.size && allTabs.isNotEmpty()) {
            pagerState.animateScrollToPage(0)
        }
    }

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
                if (alloys.isEmpty()) {
                    MultipleFABs(
                        onHelpClick = onNavigateToAddAlloy,
                        onAddClick = onNavigateToAddTransaction,
                        helpButtonDescription = stringResource(R.string.add_alloy),
                        addButtonDescription = stringResource(R.string.add_transaction),
                    )
                } else {
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
                }
            },
            content = { paddingValues ->
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.TopCenter
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
                } else if (alloys.isEmpty()) {
                    // Show "no alloys" message when there are no alloys
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.theres_no_alloys_yet_add_first),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Show normal content when there are alloys
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        if (allTabs.isNotEmpty() && pagerState.currentPage < allTabs.size) {
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

                        if (allTabs.isNotEmpty() && pagerState.currentPage < allTabs.size) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                if (page < allTabs.size) {
                                    val currentTab = allTabs[page]
                                    val filteredTransactions = when (currentTab.alloyId) {
                                        null -> transactions
                                        else -> transactionsByAlloy.entries
                                            .find { it.key.id == currentTab.alloyId }
                                            ?.value ?: emptyList()
                                    }

                                    val currentAlloy = when (currentTab.alloyId) {
                                        null -> null
                                        else -> alloys.find { it.id == currentTab.alloyId }
                                    }

                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        if (currentAlloy != null) {
                                            item {
                                                SingleAlloyBalanceCard(
                                                    selectedAlloy = currentAlloy,
                                                    transactions = transactions,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }

                                        if (filteredTransactions.isEmpty()) {
                                            item {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 32.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = if (currentTab.alloyId == null) {
                                                            stringResource(R.string.theres_no_transactions)
                                                        } else {
                                                            stringResource(R.string.no_transactions_for_alloy, currentTab.title)
                                                        },
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
                                        } else {
                                            items(filteredTransactions) { transaction ->
                                                TransactionItem(
                                                    transaction = transaction,
                                                    onClick = { onNavigateToTransactionDetail(transaction) },
                                                    onDeleteClick = {
                                                        transactionToDelete = transaction
                                                        showDeleteDialog = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Show empty state when there are no tabs
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(32.dp))
                                Text(
                                    text = stringResource(R.string.theres_no_transactions),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.add_first_transaction),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
fun MultipleFABs(
    onAddClick: () -> Unit,
    onHelpClick: () -> Unit,
    helpButtonDescription: String,
    addButtonDescription: String
) {
    Column {
        // Help/Tutorial FAB
        FloatingActionButton(
            onClick = onHelpClick,
            modifier = Modifier.padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = helpButtonDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Main Add FAB
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = addButtonDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private data class TabInfo(
    val alloyId: Long?,
    val title: String,
    val count: Int
)