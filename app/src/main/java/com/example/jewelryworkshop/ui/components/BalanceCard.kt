package com.example.jewelryworkshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.filled.Balance
//import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.domain.MetalBalance

/**
 * Компонент для отображения баланса металлов/изделий
 */
@Composable
fun BalanceCard(balance: MetalBalance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "ТЕКУЩИЙ БАЛАНС",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Вес металлов
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Icon(
//                    imageVector = Icons.Default.Balance,
//                    contentDescription = "Вес металла",
//                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Общий вес металла",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )

                    Text(
                        text = "${balance.totalWeight} грамм",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Количество изделий
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Icon(
//                    imageVector = Icons.Default.Diamond,
//                    contentDescription = "Количество изделий",
//                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Общее количество изделий",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )

                    Text(
                        text = "${balance.totalItems} шт.",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Индикатор состояния баланса (положительный/отрицательный)
            val balanceColor = when {
                balance.totalWeight > 0 -> Color(0xFF4CAF50) // Зеленый для положительного баланса
                balance.totalWeight < 0 -> Color(0xFFE53935) // Красный для отрицательного баланса
                else -> MaterialTheme.colorScheme.onPrimaryContainer // Нейтральный для нулевого баланса
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Горизонтальная линия
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Статус баланса
            Text(
                text = when {
                    balance.totalWeight > 0 -> "Положительный баланс"
                    balance.totalWeight < 0 -> "Отрицательный баланс"
                    else -> "Нулевой баланс"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = balanceColor
            )
        }
    }
}