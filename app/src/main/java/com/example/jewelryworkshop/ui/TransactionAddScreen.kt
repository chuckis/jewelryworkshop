package com.example.jewelryworkshop.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.TransactionType
import com.example.jewelryworkshop.domain.MetalAlloy
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Экран добавления новой транзакции
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAddScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    // Состояние полей формы с начальными значениями
    var dateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var weight by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.RECEIVED) }
    var description by remember { mutableStateOf("") }
    var itemsCount by remember { mutableStateOf("1") }
    var selectedAlloyId by remember { mutableStateOf<Long?>(null) } // ID выбранного сплава

    // Получаем список сплавов из ViewModel
    val alloys by viewModel.alloys.collectAsState()

    // Состояние валидации
    var weightError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var itemsCountError by remember { mutableStateOf<String?>(null) }
    var alloyError by remember { mutableStateOf<String?>(null) }

    // Получаем строки валидации один раз в Composable
    val weightErrorMsg = stringResource(com.example.jewelryworkshop.R.string.enter_correct_weight)
    val descriptionErrorMsg = stringResource(com.example.jewelryworkshop.R.string.enter_description)
    val itemsCountErrorMsg = stringResource(R.string.enter_correct_count)
    val alloyErrorMsg = stringResource(R.string.enter_alloy)

    // Диалог выбора даты и времени
    var showDatePicker by remember { mutableStateOf(false) }

    // Форматировщик даты и времени для отображения
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    // Функция валидации формы
    fun validateForm(): Boolean {
        var isValid = true

        // Проверка веса
        if (weight.isBlank() || weight.toDoubleOrNull() == null || weight.toDouble() <= 0) {
            weightError = weightErrorMsg
            isValid = false
        } else {
            weightError = null
        }

        // Проверка описания
        if (description.isBlank()) {
            descriptionError = descriptionErrorMsg
            isValid = false
        } else {
            descriptionError = null
        }

        // Проверка количества изделий
        if (itemsCount.isBlank() || itemsCount.toIntOrNull() == null || itemsCount.toInt() <= 0) {
            itemsCountError = itemsCountErrorMsg
            isValid = false
        } else {
            itemsCountError = null
        }

        // Проверка выбора сплава
        if (selectedAlloyId == null) {
            alloyError = alloyErrorMsg
            isValid = false
        } else {
            alloyError = null
        }

        return isValid
    }

    // Функция сохранения транзакции
    fun saveTransaction() {
        if (!validateForm()) return

        val weightValue = weight.toDoubleOrNull() ?: return
        val itemsCountValue = itemsCount.toIntOrNull() ?: return
        val alloyId = selectedAlloyId ?: return

        // Создание новой транзакции
        viewModel.addTransaction(
            dateTime = dateTime,
            weight = weightValue,
            type = selectedType,
            description = description,
            itemsCount = itemsCountValue,
            alloyId = alloyId,
        )

        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_transaction))},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        text = stringResource(R.string.date_time),
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
                                contentDescription = stringResource(R.string.pick_date_time)
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
                        text = stringResource(R.string.transaction_type),
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
                            text = stringResource(R.string.recieved),
                            modifier = Modifier.clickable { selectedType = TransactionType.RECEIVED }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = selectedType == TransactionType.ISSUED,
                            onClick = { selectedType = TransactionType.ISSUED }
                        )
                        Text(
                            text = stringResource(R.string.issued),
                            modifier = Modifier.clickable { selectedType = TransactionType.ISSUED }
                        )
                    }
                }
            }

            // Вес металла/изделий
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it; weightError = null },
                label = { Text(stringResource(R.string.weight_grams)) },
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
                label = { Text(stringResource(R.string.description)) },
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
                label = { Text(stringResource(R.string.quantity_of_items)) },
                modifier = Modifier.fillMaxWidth(),
                isError = itemsCountError != null,
                supportingText = {
                    if (itemsCountError != null) {
                        Text(itemsCountError!!)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            MetalAlloyDropdown(
                alloys = alloys,
                selectedAlloyId = selectedAlloyId,
                onAlloySelected = {
                    selectedAlloyId = it
                    alloyError = null
                },
                isError = alloyError != null,
                errorMessage = alloyErrorMsg
            )

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = { saveTransaction() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.ADD))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Вместо этого, в реальном приложении здесь должен быть код для отображения
    // диалога выбора даты и времени с использованием библиотеки DateTimePicker
    // или собственной реализации.
}

/**
 * Выпадающий список для выбора сплава
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalAlloyDropdown(
    alloys: List<MetalAlloy>,
    selectedAlloyId: Long?,
    onAlloySelected: (Long?) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAlloy = alloys.find { it.id == selectedAlloyId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedAlloy?.name ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.alloy)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            isError = isError,
            supportingText = {
                if (errorMessage != null) {
                    Text(errorMessage)
                }
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            alloys.forEach { alloy ->
                DropdownMenuItem(
                    text = { Text(alloy.name) },
                    onClick = {
                        onAlloySelected(alloy.id)
                        expanded = false
                    }
                )
            }
        }
    }
}