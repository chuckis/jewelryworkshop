package com.example.jewelryworkshop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jewelryworkshop.R
import com.example.jewelryworkshop.domain.MetalAlloy


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlloyEditScreen(
    alloy: MetalAlloy,
    onNavigateBack: () -> Unit,
    mainViewModel: MainViewModel = viewModel()
) {
    var alloyName by remember { mutableStateOf(alloy.name) }
    var isLoading by remember { mutableStateOf(false) }

    val errorMessage by mainViewModel.errorMessage.collectAsState()

//    LaunchedEffect(errorMessage) {
//        if (errorMessage.isNotEmpty()) {
//            // Сброс ошибки после показа
//            mainViewModel.clearErrorMessage()
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_alloy_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = alloyName,
            onValueChange = { alloyName = it },
            label = { Text(stringResource(R.string.alloy_name)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        if (errorMessage == null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

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
                onClick = {
                    if (alloyName.isNotBlank()) {
                        isLoading = true
                        val updatedAlloy = alloy.copy(name = alloyName.trim())
                        mainViewModel.updateAlloy(updatedAlloy)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading && alloyName.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.save_edition))
                }
            }
        }
    }
}