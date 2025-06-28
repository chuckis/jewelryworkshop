package com.example.jewelryworkshop.ui

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.jewelryworkshop.R
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jewelryworkshop.domain.Report
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Sample data class
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportManagementScreen(
    viewModel: ReportManagementViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

//    LaunchedEffect(uiState.errorMessage) {
//        uiState.errorMessage?.let { message ->
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//            viewModel.clearError()
//        }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearReports() },
                        enabled = uiState.generatedReports.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Reports")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Report Configuration Section
                ReportConfigurationSection(
                    uiState = uiState,
                    onAlloySelected = { alloy -> viewModel.selectAlloy(alloy) },
                    onStartPeriodChanged = { period -> viewModel.updateStartPeriod(period) },
                    onEndPeriodChanged = { period -> viewModel.updateEndPeriod(period) },
                    onCreatedByChanged = { creator -> viewModel.updateCreatedBy(creator) },
                    onSetPeriodToLastMonth = { viewModel.setPeriodToLastMonth() },
                    onSetPeriodToLastWeek = { viewModel.setPeriodToLastWeek() },
                    onSetPeriodToToday = { viewModel.setPeriodToToday() }
                )

                // Action Buttons Section
                ReportActionsSection(
                    uiState = uiState,
                    onGenerateSingleReport = { viewModel.generateSingleReport() },
                    onGenerateAllReports = { viewModel.generateReportsForAllAlloys() },
                    onLoadSummary = { viewModel.loadReportSummary() }
                )

                // Report Summary Section
                if (uiState.reportSummary != null) {
                    ReportSummarySection(
                        summary = uiState.reportSummary,
                        isLoading = uiState.isLoadingSummary
                    )
                }

                // Generated Reports Section
                if (uiState.generatedReports.isNotEmpty()) {
                    GeneratedReportsSection(
                        reports = uiState.generatedReports,
                        onExportReport = { report ->
                            scope.launch {
                                try {
                                    exportReportAsCsv(context, report)
                                    Toast.makeText(context, "Report exported successfully", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportConfigurationSection(
    uiState: ReportManagementUiState,
    onAlloySelected: (MetalAlloy?) -> Unit,
    onStartPeriodChanged: (LocalDateTime) -> Unit,
    onEndPeriodChanged: (LocalDateTime) -> Unit,
    onCreatedByChanged: (String) -> Unit,
    onSetPeriodToLastMonth: () -> Unit,
    onSetPeriodToLastWeek: () -> Unit,
    onSetPeriodToToday: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Report Configuration",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Alloy Selection
            MetalAlloyDropdown(
                alloys = uiState.availableAlloys,
                selectedAlloyId = uiState.selectedAlloy?.id,
                onAlloySelected = { id ->
                    val alloy = uiState.availableAlloys.find { it.id == id }
                    onAlloySelected(alloy)
                }
            )

            // Period Selection
            Text(
                text = "Report Period",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            // Quick Period Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSetPeriodToToday,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Today")
                }
                OutlinedButton(
                    onClick = onSetPeriodToLastWeek,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Last Week")
                }
                OutlinedButton(
                    onClick = onSetPeriodToLastMonth,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Last Month")
                }
            }

            // Date/Time Pickers (simplified representation)
            DateTimePicker(
                label = "Start Period",
                dateTime = uiState.startPeriod,
                onDateTimeChanged = onStartPeriodChanged
            )

            DateTimePicker(
                label = "End Period",
                dateTime = uiState.endPeriod,
                onDateTimeChanged = onEndPeriodChanged
            )

            // Created By Field
            OutlinedTextField(
                value = uiState.createdBy,
                onValueChange = onCreatedByChanged,
                label = { Text("Created By") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )
        }
    }
}

@Composable
private fun DateTimePicker(
    label: String,
    dateTime: LocalDateTime,
    onDateTimeChanged: (LocalDateTime) -> Unit
) {
    // Simplified date/time picker - in real implementation you'd use proper date/time pickers
    OutlinedTextField(
        value = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
        onValueChange = { /* Handle date/time parsing */ },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
        }
    )
}

@Composable
private fun ReportActionsSection(
    uiState: ReportManagementUiState,
    onGenerateSingleReport: () -> Unit,
    onGenerateAllReports: () -> Unit,
    onLoadSummary: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGenerateSingleReport,
                    enabled = !uiState.isGeneratingReport && uiState.selectedAlloy != null,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isGeneratingReport) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Description, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Report")
                }

                Button(
                    onClick = onGenerateAllReports,
                    enabled = !uiState.isGeneratingReport,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ListAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("All Alloys")
                }
            }

            Button(
                onClick = onLoadSummary,
                enabled = !uiState.isLoadingSummary && uiState.selectedAlloy != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                if (uiState.isLoadingSummary) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                } else {
                    Icon(Icons.Default.Analytics, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Load Summary")
            }
        }
    }
}

@Composable
private fun ReportSummarySection(
    summary: ReportSummary,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Report Summary",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryItem(
                        label = "Total Records",
                        value = summary.totalRecords.toString()
                    )
                    SummaryItem(
                        label = "Total Amount",
                        value = summary.totalAmount.toString()
                    )
                    SummaryItem(
                        label = "Average",
                        value = summary.averageAmount.toString()
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GeneratedReportsSection(
    reports: List<Report>,
    onExportReport: (Report) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generated Reports (${reports.size})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reports) { report ->
                    ReportItem(
                        report = report,
                        onExport = { onExportReport(report) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportItem(
    report: Report,
    onExport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = report.alloyName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Created: ${report.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "By: ${report.createdBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Records: ${report.data.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            FilledTonalButton(
                onClick = onExport,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV")
            }
        }
    }
}

// CSV Export function adapted for Report data
private suspend fun exportReportAsCsv(context: Context, report: Report) {
    try {
        val csvContent = createReportCsvContent(report)
        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val fileName = "report_${report.alloyName.replace(" ", "_")}_$timestamp.csv"

        // Create file in app's external files directory
        val file = java.io.File(context.getExternalFilesDir(null), fileName)
        file.writeText(csvContent)

        // Create URI for sharing
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Create share intent
        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "Metal Alloy Report - ${report.alloyName}")
            putExtra(android.content.Intent.EXTRA_TEXT, "Please find the metal alloy report attached.")
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Report"))

    } catch (e: Exception) {
        android.util.Log.e("ReportExport", "Failed to export report CSV", e)
        throw e
    }
}

private fun createReportCsvContent(report: Report): String {
    val csvBuilder = StringBuilder()

    // Add header with report metadata
    csvBuilder.append("Metal Alloy Report\n")
    csvBuilder.append("Alloy: ${escapeCsvField(report.alloyName)}\n")
    csvBuilder.append("Created: ${report.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}\n")
    csvBuilder.append("Created By: ${escapeCsvField(report.createdBy)}\n")
    csvBuilder.append("Period: ${report.startPeriod.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))} to ${report.endPeriod.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}\n")
    csvBuilder.append("\n")

    // Add data header row
    csvBuilder.append("ID,Item Name,Category,Amount,Date,Description\n")

    // Add data rows
    report.data.forEach { item ->
        csvBuilder.append("${item.id},")
        csvBuilder.append("${escapeCsvField(item.name)},")
        csvBuilder.append("${escapeCsvField(item.category)},")
        csvBuilder.append("${item.amount},")
        csvBuilder.append("${item.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))},")
        csvBuilder.append("${escapeCsvField(item.description ?: "")}\n")
    }

    return csvBuilder.toString()
}

private fun escapeCsvField(field: String): String {
    return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
        "\"${field.replace("\"", "\"\"")}\""
    } else {
        field
    }
}