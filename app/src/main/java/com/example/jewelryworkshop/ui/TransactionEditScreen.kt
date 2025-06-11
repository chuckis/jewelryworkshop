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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import java.time.format.DateTimeFormatter


// TODO() non DRY!!!
/**
 * Экран редактирования транзакции
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(
    viewModel: MainViewModel,
    existingTransaction: Transaction,
    onNavigateBack: () -> Unit
) {

    val alloys by viewModel.alloys.collectAsState()
    // Состояние полей формы с начальными значениями из существующей транзакции
    var dateTime by remember { mutableStateOf(existingTransaction.dateTime) }
    var weight by remember { mutableStateOf(existingTransaction.weight.toString()) }
    var selectedType by remember { mutableStateOf(existingTransaction.type) }
    var description by remember { mutableStateOf(existingTransaction.description) }
    var itemsCount by remember { mutableStateOf(existingTransaction.itemsCount.toString()) }
    var metalAlloy by remember { mutableStateOf(existingTransaction.alloy) }

    // Состояние валидации
    var weightError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var itemsCountError by remember { mutableStateOf<String?>(null) }
    var alloyError by remember { mutableStateOf<String?>(null) }

    // Получаем строки валидации один раз в Composable
    val weightErrorMsg = stringResource(R.string.enter_correct_weight)
    val descriptionErrorMsg = stringResource(R.string.enter_description)
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

        // Проверка выбора сплава (используем metalAlloy вместо selectedAlloyId)
        if (metalAlloy.id <= 0) {
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

        // Обновление существующей транзакции
        viewModel.updateTransaction(
            transactionId = existingTransaction.id,
            dateTime = dateTime,
            weight = weightValue,
            type = selectedType,
            description = description,
            itemsCount = itemsCountValue,
            alloyId = metalAlloy.id
        )
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_transaction))},
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

            // TransactionType
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
                label = { Text(stringResource(R.string.description))},
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

            // Выбор сплава
            MetalAlloyDropdown(
                alloys = alloys,
                selectedAlloy = metalAlloy,
                onAlloySelected = {
                    metalAlloy = it
                    alloyError = null // Сбрасываем ошибку при выборе сплава
                },
                isError = alloyError != null,
                errorMessage = alloyError
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
                    Text(stringResource(R.string.save_edition))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalAlloyDropdown(
    alloys: List<MetalAlloy>,
    selectedAlloy: MetalAlloy,
    onAlloySelected: (MetalAlloy) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedAlloy.name,
                onValueChange = { },
                readOnly = true,
                label = { Text("Сплав металла") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = isError
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                alloys.forEach { alloy ->
                    DropdownMenuItem(
                        text = { Text(alloy.name) },
                        onClick = {
                            onAlloySelected(alloy)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Отображение ошибки валидации
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}