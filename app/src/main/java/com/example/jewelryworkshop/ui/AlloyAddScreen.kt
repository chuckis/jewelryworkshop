package com.example.jewelryworkshop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * Экран добавления нового сплава
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlloyAddScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    // Состояние полей формы
    var alloyName by remember { mutableStateOf("") }

    // Состояние валидации
    var nameError by remember { mutableStateOf<String?>(null) }

    // Состояние загрузки
    var isLoading by remember { mutableStateOf(false) }

    // Получаем список существующих сплавов для проверки дубликатов
    val existingAlloys by viewModel.alloys.collectAsState()

    // Отслеживаем сообщения об ошибках из ViewModel
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Показываем снэкбар при ошибке
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // Здесь можно показать снэкбар или другое уведомление
            // После показа очищаем ошибку
            viewModel.clearErrorMessage()
        }
    }

    // Функция валидации формы
    fun validateForm(): Boolean {
        var isValid = true

        // Проверка имени сплава
        when {
            alloyName.isBlank() -> {
                nameError = "Введите название сплава"
                isValid = false
            }
            alloyName.length < 2 -> {
                nameError = "Название должно содержать минимум 2 символа"
                isValid = false
            }
            alloyName.length > 50 -> {
                nameError = "Название не должно превышать 50 символов"
                isValid = false
            }
            existingAlloys.any { it.name.equals(alloyName.trim(), ignoreCase = true) } -> {
                nameError = "Сплав с таким названием уже существует"
                isValid = false
            }
            else -> {
                nameError = null
            }
        }

        return isValid
    }

    // Функция сохранения сплава
    fun saveAlloy() {
        if (!validateForm()) return

        isLoading = true

        // Добавляем новый сплав через ViewModel
        viewModel.addAlloy(alloyName.trim())

        // После успешного добавления возвращаемся назад
        // (в реальном приложении лучше отслеживать результат операции)
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый сплав") },
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

            // Информационная карточка
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Добавление сплава",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Введите название нового металлического сплава. Название должно быть уникальным.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Поле ввода названия сплава
            OutlinedTextField(
                value = alloyName,
                onValueChange = {
                    alloyName = it
                    nameError = null
                },
                label = { Text("Название сплава") },
                placeholder = { Text("Например: Золото 585, Серебро 925") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(
                            text = nameError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = "Введите от 2 до 50 символов",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                singleLine = true,
                enabled = !isLoading
            )

            // Показать список существующих сплавов для справки
            if (existingAlloys.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Существующие сплавы:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        existingAlloys.take(5).forEach { alloy ->
                            Text(
                                text = "• ${alloy.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        if (existingAlloys.size > 5) {
                            Text(
                                text = "... и еще ${existingAlloys.size - 5}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Отмена")
                }

                Button(
                    onClick = { saveAlloy() },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && alloyName.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Добавить")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}