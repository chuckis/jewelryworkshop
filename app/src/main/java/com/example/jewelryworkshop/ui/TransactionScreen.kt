package com.example.jewelryworkshop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jewelryworkshop.app.domain.model.MetalAlloy
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import com.jewelryworkshop.ui.MainViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Экран добавления/редактирования транзакции
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: MainViewModel,
    existingTransaction: Transaction? = null,
    onNavigateBack: () -> Unit
) {
    val isEditing = existingTransaction != null
    val title = if (isEditing) "Редактировать операцию" else "Новая операция"

    // Состояние полей формы
    var dateTime by remember { mutableStateOf(existingTransaction?.dateTime ?: LocalDateTime.now()) }
    var weight by remember { mutableStateOf((existingTransaction?.weight ?: 0.0).toString()) }
    var selectedType by remember { mutableStateOf(existingTransaction?.type ?: TransactionType.RECEIVED) }
    var description by remember { mutableStateOf(existingTransaction?.description ?: "") }
    var itemsCount by remember { mutableStateOf((existingTransaction?.itemsCount ?: 1).toString()) }
    var metalAlloy by remember { mutableStateOf(existingTransaction?.alloy ?: "") }
    // Состояние валидации
    var weightError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var itemsCountError by remember { mutableStateOf<String?>(null) }

    // Диалог выбора даты и времени
    var showDatePicker by remember { mutableStateOf(false) }

    // Форматировщик даты и времени для отображения
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    // Функция валидации формы
    fun validateForm(): Boolean {
        var isValid = true

        // Проверка веса
        if (weight.isBlank() || weight.toDoubleOrNull() == null || weight.toDouble() <= 0) {
            weightError = "Введите корректный вес (больше 0)"
            isValid = false
        } else {
            weightError = null
        }

        // Проверка описания
        if (description.isBlank()) {
            descriptionError = "Введите описание"
            isValid = false
        } else {
            descriptionError = null
        }

        // Проверка количества изделий
        if (itemsCount.isBlank() || itemsCount.toIntOrNull() == null || itemsCount.toInt() <= 0) {
            itemsCountError = "Введите корректное количество (больше 0)"
            isValid = false
        } else {
            itemsCountError = null
        }

        return isValid
    }

    // Функция сохранения транзакции
    fun saveTransaction() {
        if (!validateForm()) return

        val weightValue = weight.toDoubleOrNull() ?: return
        val itemsCountValue = itemsCount.toIntOrNull() ?: return

        if (isEditing && existingTransaction != null) {
            // Обновление существующей транзакции
            val updatedTransaction = existingTransaction.copy(
                dateTime = dateTime,
                weight = weightValue,
                type = selectedType,
                description = description,
                itemsCount = itemsCountValue,
                alloy = metalAlloy as MetalAlloy,
            )
            viewModel.updateTransaction(updatedTransaction)
        } else {
            // Создание новой транзакции
            viewModel.addTransaction(
                dateTime = dateTime,
                weight = weightValue,
                type = selectedType,
                description = description,
                itemsCount = itemsCountValue,
                metalAlloy = metalAlloy as MetalAlloy,

            )
        }

        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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

            // Выбор даты и времени
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Дата и время",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateFormatter.format(dateTime),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Выбрать дату и время"
                            )
                        }
                    }
                }
            }

            // Выбор типа операции
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Тип операции",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == TransactionType.RECEIVED,
                            onClick = { selectedType = TransactionType.RECEIVED }
                        )
                        Text(
                            text = "Получено",
                            modifier = Modifier.clickable { selectedType = TransactionType.RECEIVED }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = selectedType == TransactionType.ISSUED,
                            onClick = { selectedType = TransactionType.ISSUED }
                        )
                        Text(
                            text = "Выдано",
                            modifier = Modifier.clickable { selectedType = TransactionType.ISSUED }
                        )
                    }
                }
            }

            // Вес металла/изделий
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it; weightError = null },
                label = { Text("Вес (грамм)") },
                modifier = Modifier.fillMaxWidth(),
                isError = weightError != null,
                supportingText = {
                    if (weightError != null) {
                        Text(weightError!!)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Описание
            OutlinedTextField(
                value = description,
                onValueChange = { description = it; descriptionError = null },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(),
                isError = descriptionError != null,
                supportingText = {
                    if (descriptionError != null) {
                        Text(descriptionError!!)
                    }
                }
            )

            // Количество изделий
            OutlinedTextField(
                value = itemsCount,
                onValueChange = { itemsCount = it; itemsCountError = null },
                label = { Text("Количество изделий") },
                modifier = Modifier.fillMaxWidth(),
                isError = itemsCountError != null,
                supportingText = {
                    if (itemsCountError != null) {
                        Text(itemsCountError!!)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            DropdownField()

            // Кнопка сохранения
            Button(
                onClick = { saveTransaction() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text(if (isEditing) "Сохранить изменения" else "Добавить операцию")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // TODO: Реализовать DateTimePicker (в этом примере опущено для краткости)
    // Вместо этого, в реальном приложении здесь должен быть код для отображения
    // диалога выбора даты и времени с использованием библиотеки DateTimePicker
    // или собственной реализации.
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Select an option") }
    val options = listOf("Option 1", "Option 2", "Option 3")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Dropdown") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}
