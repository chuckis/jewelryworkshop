package com.example.jewelryworkshop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import java.time.format.DateTimeFormatter

/**
 * Экран просмотра деталей транзакции
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transaction: Transaction,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    // Форматировщик даты и времени для отображения
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали операции") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
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
                onClick = onNavigateToEdit,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Карточка с общей информацией
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Общая информация",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        label = "Дата и время:",
                        value = dateFormatter.format(transaction.dateTime)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Тип операции:",
                        value = when (transaction.type) {
                            TransactionType.RECEIVED -> "Получено"
                            TransactionType.ISSUED -> "Выдано"
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Описание:",
                        value = transaction.description
                    )
                }
            }

            // Карточка с информацией о материале
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Информация о материале",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        label = "Вес:",
                        value = "${transaction.weight} г"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Сплав:",
                        value = transaction.alloy.toString()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        label = "Количество изделий:",
                        value = transaction.itemsCount.toString()
                    )
                }
            }

            // Дополнительная информация (если есть ID)
            transaction.id?.let { id ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Системная информация",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow(
                            label = "ID операции:",
                            value = id.toString()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

