package com.example.jewelryworkshop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.jewelryworkshop.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.domain.MetalAlloy

/**
 * Экран управления сплавами (просмотр, редактирование, удаление)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlloyManagementScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddAlloy: () -> Unit,
    onNavigateToEditAlloy: (MetalAlloy) -> Unit
) {
    // Получаем список сплавов
    val alloys by viewModel.alloys.collectAsState()

    // Состояние для диалога подтверждения удаления
    var alloyToDelete by remember { mutableStateOf<MetalAlloy?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.alloy_management)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddAlloy,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_alloy)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (alloys.isEmpty()) {
                // Показываем заглушку если нет сплавов
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_alloys),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.add_alloy),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Показываем список сплавов
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                val totalAlloysStr = stringResource(R.string.total_alloys)
                                Text(
                                    text = "${totalAlloysStr}: ${alloys.size}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.manage_list),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(alloys) { alloy ->
                        AlloyListItem(
                            alloy = alloy,
                            onEditClick = {
                                 onNavigateToEditAlloy(alloy)
                            },
                            onDeleteClick = {
                                alloyToDelete = alloy
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog && alloyToDelete != null) {
        val areYouShureYouDeleteAlloy = stringResource(R.string.are_u_sure_delete)
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                alloyToDelete = null
            },
            title = { Text(stringResource(R.string.delete)) },
            text = {
                Text("${areYouShureYouDeleteAlloy} \"${alloyToDelete!!.name}\"?!")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        alloyToDelete?.let { viewModel.deleteAlloy(it) }
                        showDeleteDialog = false
                        alloyToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        alloyToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/**
 * Элемент списка сплавов
 */
@Composable
fun AlloyListItem(
    alloy: MetalAlloy,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alloy.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ID: ${alloy.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}