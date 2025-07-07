package com.example.jewelryworkshop.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MultipleFABs(
    onAddClick: () -> Unit,
    onHelpClick: () -> Unit,
    helpButtonDescription: String,
    addButtonDescription: String
) {
    Column {
        // Help/Tutorial FAB
        FloatingActionButton(
            onClick = onHelpClick,
            modifier = Modifier.padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = helpButtonDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Main Add FAB
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = addButtonDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}