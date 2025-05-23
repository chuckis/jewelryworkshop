package com.example.jewelryworkshop.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import java.time.format.DateTimeFormatter

/**
 * Компонент для отображения одной транзакции в списке
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: (Transaction) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val isReceived = transaction.type == TransactionType.RECEIVED
    val indicatorColor = if (isReceived) Color(0xFF4CAF50) else Color(0xFFE53935)
    val typeText = if (isReceived) "Получено" else "Выдано"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(transaction) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Индикатор типа транзакции (зеленый для "получено", красный для "выдано")
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 48.dp)
                    .background(color = indicatorColor, shape = RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Основная информация о транзакции
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Дата и время транзакции
                    Text(
                        text = dateFormatter.format(transaction.dateTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Тип транзакции и вес
                    Text(
                        text = "$typeText ${transaction.weight} г",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = indicatorColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Описание
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Количество изделий
                Text(
                    text = "Количество: ${transaction.itemsCount} шт.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Кнопки управления
            Row {
                IconButton(onClick = { onClick(transaction) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { onDeleteClick(transaction.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}