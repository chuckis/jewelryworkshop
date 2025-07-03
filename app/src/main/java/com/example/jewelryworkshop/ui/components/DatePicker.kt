package com.example.jewelryworkshop.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.example.jewelryworkshop.R
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.Instant
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactDatePickerDialog(
    initialDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit,
    minDate: LocalDateTime? = null // Добавляем параметр минимальной даты
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val dateToCheck = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                val isNotInFuture = !dateToCheck.isAfter(LocalDate.now())

                val isNotBeforeMin = minDate?.let { min ->
                    !dateToCheck.isBefore(min.toLocalDate())
                } ?: true

                return isNotInFuture && isNotBeforeMin
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                Text(stringResource(R.string.select))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}