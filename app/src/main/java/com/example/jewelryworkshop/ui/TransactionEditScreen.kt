package com.example.jewelryworkshop.ui

import MetalAlloyDropdown
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import com.example.jewelryworkshop.ui.components.CompactDatePickerDialog

/**
 * Edit Transaction Screen
 */
@SuppressLint("MemberExtensionConflict")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(
    viewModel: MainViewModel,
    existingTransaction: Transaction,
    onNavigateBack: () -> Unit
) {

    val alloys by viewModel.alloys.collectAsState()
    var dateTime by remember { mutableStateOf(existingTransaction.dateTime) }
    var weight by remember { mutableStateOf(existingTransaction.weight.toString()) }
    var selectedType by remember { mutableStateOf(existingTransaction.type) }
    var description by remember { mutableStateOf(existingTransaction.description) }
    var itemsCount by remember { mutableStateOf(existingTransaction.itemsCount.toString()) }
    var selectedAlloyId by remember { mutableStateOf<Long?>(existingTransaction.alloy.id) }

    var weightError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var itemsCountError by remember { mutableStateOf<String?>(null) }
    var alloyError by remember { mutableStateOf<String?>(null) }

    val weightErrorMsg = stringResource(R.string.enter_correct_weight)
    val descriptionErrorMsg = stringResource(R.string.enter_description)
    val itemsCountErrorMsg = stringResource(R.string.enter_correct_count)
    val alloyErrorMsg = stringResource(R.string.enter_alloy)

    var showDatePicker by remember { mutableStateOf(false) }

    fun validateForm(): Boolean {
        var isValid = true

        if (weight.isBlank() || weight.toDoubleOrNull() == null || weight.toDouble() <= 0) {
            weightError = weightErrorMsg
            isValid = false
        } else {
            weightError = null
        }

        if (description.isBlank()) {
            descriptionError = descriptionErrorMsg
            isValid = false
        } else {
            descriptionError = null
        }

        if (itemsCount.isBlank() || itemsCount.toIntOrNull() == null || itemsCount.toInt() <= 0) {
            itemsCountError = itemsCountErrorMsg
            isValid = false
        } else {
            itemsCountError = null
        }

        if (selectedAlloyId == null) {
            alloyError = alloyErrorMsg
            isValid = false
        } else {
            alloyError = null
        }

        return isValid
    }

    fun saveTransaction() {
        if (!validateForm()) return

        val weightValue = weight.toDoubleOrNull() ?: return
        val itemsCountValue = itemsCount.toIntOrNull() ?: return

        val selectedAlloyIdValue = selectedAlloyId ?: return

        viewModel.updateTransaction(
            transactionId = existingTransaction.id,
            dateTime = dateTime,
            weight = weightValue,
            type = selectedType,
            description = description,
            itemsCount = itemsCountValue,
            alloyId = selectedAlloyIdValue
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

            MetalAlloyDropdown(
                alloys = alloys,
                selectedAlloyId = selectedAlloyId,
                onAlloySelected = {
                    selectedAlloyId = it
                    alloyError = null
                },
                isError = alloyError != null,
                errorMessage = alloyError
            )

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
    // Date Picker Dialog
    if (showDatePicker) {
        CompactDatePickerDialog(
            initialDate = dateTime,
            onDateSelected = { selectedDateTime ->
                dateTime = selectedDateTime
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}