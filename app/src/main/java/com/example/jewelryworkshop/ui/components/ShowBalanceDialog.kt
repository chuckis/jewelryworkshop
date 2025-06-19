package com.example.jewelryworkshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import com.example.jewelryworkshop.R

/**
 * Data class для отображения баланса одного сплава
 */
data class SingleAlloyBalance(
    val alloy: MetalAlloy,
    val currentBalance: Double,
    val totalIncoming: Double,
    val totalOutgoing: Double,
    val transactionCount: Int
)

@Composable
fun SingleAlloyBalanceCard(
    selectedAlloy: MetalAlloy,
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    // Вычисляем баланс для выбранного сплава
    val balance = remember(transactions, selectedAlloy) {
        calculateSingleAlloyBalance(transactions, selectedAlloy)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Заголовок
            Text(
                text = stringResource(R.string.balance) + ": ${balance.alloy.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (balance.transactionCount == 0) {
                // Если нет транзакций
                Text(
                    text = stringResource(R.string.no_transactions_for_alloy),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            } else {
                // Текущий баланс
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.current_balance) + ":",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${String.format("%.2f", balance.currentBalance)} г",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            balance.currentBalance > 0 -> MaterialTheme.colorScheme.primary
                            balance.currentBalance < 0 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                // Детали баланса
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Приход
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.incoming),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.2f", balance.totalIncoming)}" + stringResource(R.string.grams),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Разделитель
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Расход
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.outgoing),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.2f", balance.totalOutgoing)} г",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Разделитель
                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Количество транзакций
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.transactions),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${balance.transactionCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Функция для вычисления баланса одного сплава
 */
private fun calculateSingleAlloyBalance(
    transactions: List<Transaction>,
    selectedAlloy: MetalAlloy
): SingleAlloyBalance {
    // Фильтруем транзакции только для выбранного сплава
    val alloyTransactions = transactions.filter { it.alloy == selectedAlloy }

    val incoming = alloyTransactions
        .filter { it.type == TransactionType.RECEIVED }
        .sumOf { it.weight }

    val outgoing = alloyTransactions
        .filter { it.type == TransactionType.ISSUED }
        .sumOf { it.weight }

    return SingleAlloyBalance(
        alloy = selectedAlloy,
        currentBalance = incoming - outgoing,
        totalIncoming = incoming,
        totalOutgoing = outgoing,
        transactionCount = alloyTransactions.size
    )
}