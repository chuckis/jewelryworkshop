package com.example.jewelryworkshop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jewelryworkshop.R
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Sample data class
data class ReportItem(
    val id: Int,
    val name: String,
    val category: String,
    val amount: Double,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportManagementScreen(
    onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Sample data
    val reportData = remember {
        listOf(
            ReportItem(1, "Office Supplies", "Equipment", 245.50, "2024-06-20"),
            ReportItem(2, "Software License", "Software", 1299.99, "2024-06-21"),
            ReportItem(3, "Team Lunch", "Food", 87.30, "2024-06-22"),
            ReportItem(4, "Client Meeting", "Travel", 156.75, "2024-06-23"),
            ReportItem(5, "Marketing Materials", "Marketing", 432.20, "2024-06-24"),
            ReportItem(6, "Server Hosting", "Infrastructure", 89.99, "2024-06-25"),
            ReportItem(7, "Training Course", "Education", 299.00, "2024-06-26"),
            ReportItem(8, "Office Rent", "Facilities", 2500.00, "2024-06-27")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление отчетами") },
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
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Expense Report",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        scope.launch {
                            isExporting = true
                            try {
                                exportAndShareCsv(context, reportData)
                                snackbarMessage = "CSV exported successfully!"
                                showSnackbar = true
                            } catch (e: Exception) {
                                snackbarMessage = "Export failed: ${e.message}"
                                showSnackbar = true
                            } finally {
                                isExporting = false
                            }
                        }
                    },
                    enabled = !isExporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exporting...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export CSV")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Items: ${reportData.size}")
                        Text("Total Amount: $${reportData.sumOf { it.amount }}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data List
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Name", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                            Text("Category", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                            Text("Amount", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("Date", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider()
                    }

                    // Data rows
                    items(reportData) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.name, modifier = Modifier.weight(2f))
                            Text(item.category, modifier = Modifier.weight(1.5f))
                            Text("$${item.amount}", modifier = Modifier.weight(1f))
                            Text(item.date, modifier = Modifier.weight(1f))
                        }
                        if (item != reportData.last()) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                        }
                    }
                }
            }
        }

        // Snackbar
        if (showSnackbar) {
            LaunchedEffect(snackbarMessage) {
                kotlinx.coroutines.delay(3000)
                showSnackbar = false
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    }
}

// CSV Export Functions
fun createCsvContent(reportData: List<ReportItem>): String {
    val csvBuilder = StringBuilder()

    // Add header row
    csvBuilder.append("ID,Name,Category,Amount,Date\n")

    // Add data rows
    reportData.forEach { item ->
        csvBuilder.append("${item.id},")
        csvBuilder.append("${escapeCsvField(item.name)},")
        csvBuilder.append("${escapeCsvField(item.category)},")
        csvBuilder.append("${item.amount},")
        csvBuilder.append("${item.date}\n")
    }

    return csvBuilder.toString()
}

fun escapeCsvField(field: String): String {
    return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
        "\"${field.replace("\"", "\"\"")}\""
    } else {
        field
    }
}

suspend fun exportAndShareCsv(context: Context, data: List<ReportItem>) {
    try {
        val csvContent = createCsvContent(data)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "expense_report_$timestamp.csv"

        // Create file in app's cache directory
        val file = File(context.cacheDir, fileName)
        file.writeText(csvContent)

        // Create URI for sharing
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Create share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Report Export")
            putExtra(Intent.EXTRA_TEXT, "Please find the expense report attached.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share CSV Report"))

    } catch (e: Exception) {
        Log.e("CsvExport", "Failed to export CSV", e)
        throw e
    }
}