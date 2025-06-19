package com.example.jewelryworkshop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.jewelryworkshop.R
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
    var alloyName by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    val existingAlloys by viewModel.alloys.collectAsState()

    val errorMessage by viewModel.errorMessage.collectAsState()

    // Показываем снэкбар при ошибке
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // Здесь можно показать снэкбар или другое уведомление
            // После показа очищаем ошибку
            viewModel.clearErrorMessage()
        }
    }
    val nameErrorEnterAlloyNameStr = stringResource(R.string.enter_alloy_name)
    val nameErrorMoreTwoLettersStr = stringResource(R.string.more_than_two)
    val nameErrorLesserThanFiftyStr = stringResource(R.string.lesser_than_fifty)
    val nameErrorNotUniqueNameStr = stringResource(R.string.not_unique_name)

    // Функция валидации формы
    fun validateForm(): Boolean {
        var isValid = true

        // Проверка имени сплава
        when {
            alloyName.isBlank() -> {
                nameError = nameErrorEnterAlloyNameStr
                isValid = false
            }
            alloyName.length < 2 -> {
                nameError = nameErrorMoreTwoLettersStr
                isValid = false
            }
            alloyName.length > 10 -> {
                nameError = nameErrorLesserThanFiftyStr
                isValid = false
            }
            existingAlloys.any { it.name.equals(alloyName.trim(), ignoreCase = true) } -> {
                nameError = nameErrorNotUniqueNameStr
                isValid = false
            }
            else -> {
                nameError = null
            }
        }

        return isValid
    }

    fun saveAlloy() {
        if (!validateForm()) return

        isLoading = true

        viewModel.addAlloy(alloyName.trim())

        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_alloy)) },
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
                        text = stringResource(R.string.add_alloy),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.enter_alloy_name) + stringResource(R.string.must_b_unique),
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
                label = { Text(stringResource(R.string.alloy_name)) },
                placeholder = { Text(stringResource(R.string.for_example)) },
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
                            text = stringResource(R.string.enter_from_two_up_to_ten_symbols),
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
                            text = stringResource(R.string.existing_alloys),
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

                        if (existingAlloys.size > 3) {
                            Text(
                                text = "... + ${existingAlloys.size - 3}",
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
                    Text(stringResource(R.string.cancel))
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
                        Text(stringResource(R.string.add_alloy))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}