package com.example.jewelryworkshop.ui

import android.R.attr.enabled
import android.R.attr.type
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.jewelryworkshop.R
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jewelryworkshop.domain.Report
import com.example.jewelryworkshop.domain.MetalAlloy
import java.time.LocalDateTime
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import com.example.jewelryworkshop.ui.components.CompactDatePickerDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportManagementScreen(
    viewModel: ReportManagementViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.report_management)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.clear_reports)) },
                            enabled = uiState.generatedReports.isNotEmpty(),
                            onClick = {
                                viewModel.clearReports()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.day_report)) },
                            onClick = {
                                viewModel.setPeriodToToday()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.week_report)) },
                            onClick = {
                                viewModel.setPeriodToLastWeek()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.month_report)) },
                            onClick = {
                                viewModel.setPeriodToLastMonth()
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ))
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
                )

                // Action Buttons Section
                ReportActionsSection(
                    uiState = uiState,
                    onGenerateSingleReport = { viewModel.generateSingleReport() },
                )
                // Generated Reports Section
                if (uiState.generatedReports.isNotEmpty()) {
                    GeneratedReportsSection(
                        reports = uiState.generatedReports,
                        onExportReport = { report ->
                            scope.launch {
                                try {
                                    exportReportAsCsv(context, report)
                                    Toast.makeText(context, context.getString(R.string.report_exported_successfully), Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, context.getString(R.string.export_failed, e.message), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlloySelectionDropdown(
    alloys: List<MetalAlloy>,
    selectedAlloy: MetalAlloy?,
    onAlloySelected: (MetalAlloy?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedAlloy?.name ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.select_alloy)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(Icons.Default.Build, contentDescription = null)
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
                        onAlloySelected(alloy)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportConfigurationSection(
    uiState: ReportManagementUiState,
    onAlloySelected: (MetalAlloy?) -> Unit,
    onStartPeriodChanged: (LocalDateTime) -> Unit,
    onEndPeriodChanged: (LocalDateTime) -> Unit,
    onCreatedByChanged: (String) -> Unit,
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.report_configuration),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Alloy Selection
            AlloySelectionDropdown(
                alloys = uiState.availableAlloys,
                selectedAlloy = uiState.selectedAlloy,
                onAlloySelected = onAlloySelected
            )

            // Period Selection
            Text(
                text = stringResource(R.string.report_period),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            // Start Date Picker
            OutlinedTextField(
                value = formatLocalDateTime(uiState.startPeriod),
                onValueChange = { },
                label = { Text(stringResource(R.string.start_period)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_date)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartDatePicker = true }
            )

            // End Date Picker
            OutlinedTextField(
                value = formatLocalDateTime(uiState.endPeriod),
                onValueChange = { },
                label = { Text(stringResource(R.string.end_period)) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_date)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndDatePicker = true }
            )

            // Created By Field
            OutlinedTextField(
                value = uiState.createdBy,
                onValueChange = onCreatedByChanged,
                label = { Text(stringResource(R.string.created_by)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )
        }
    }

    // Date Picker Dialogs
    if (showStartDatePicker) {
        CompactDatePickerDialog(
            initialDate = uiState.startPeriod,
            onDateSelected = { dateTime ->
                onStartPeriodChanged(dateTime)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        CompactDatePickerDialog(
            initialDate = uiState.endPeriod,
            minDate = uiState.startPeriod,
            onDateSelected = { dateTime ->
                onEndPeriodChanged(dateTime)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@Composable
private fun ReportActionsSection(
    uiState: ReportManagementUiState,
    onGenerateSingleReport: () -> Unit,
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
                text = stringResource(R.string.actions),
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
                        Text(stringResource(R.string.generate_report))
                    }

                }

            }
        }
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
                    Icons.Default.MailOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.generated_reports, reports.size),
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
                    text = report.metalAlloy.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.created_label, report.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.by_label, report.createdBy as String),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.records_label, report.transactions?.size ?: 0),
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
                Text(stringResource(R.string.export_csv))
            }
        }
    }
}

// CSV Export function adapted for Report data
private fun exportReportAsCsv(context: Context, report: Report) {
    try {
        val csvContent = createReportCsvContent(context, report)
        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val fileName = "report_${report.metalAlloy.name.replace(" ", "_")}_$timestamp.csv"

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
            putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.share_report_subject, report.metalAlloy.name))
            putExtra(android.content.Intent.EXTRA_TEXT, context.getString(R.string.share_report_text))
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(android.content.Intent.createChooser(shareIntent, context.getString(R.string.share_report)))

    } catch (e: Exception) {
        android.util.Log.e("ReportExport", "Failed to export report CSV", e)
        throw e
    }
}

@SuppressLint("MemberExtensionConflict")
private fun createReportCsvContent(context: Context, report: Report): String {
    val csvBuilder = StringBuilder()

    // Add header with report metadata
    csvBuilder.append("${context.getString(R.string.csv_metal_alloy_report)}\n")
    csvBuilder.append("${context.getString(R.string.csv_alloy_label, escapeCsvField(report.metalAlloy.name))}\n")
    csvBuilder.append("${context.getString(R.string.csv_created_label, report.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: "")}\n")
    csvBuilder.append("${context.getString(R.string.csv_created_by_label, escapeCsvField(report.createdBy as String))}\n")
    csvBuilder.append("${context.getString(R.string.csv_period_label,
        report.startPeriod?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "",
        report.endPeriod?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "")}\n")
    csvBuilder.append("\n")

    // Add data header row
    csvBuilder.append("${context.getString(R.string.csv_header_id)},")
    csvBuilder.append("${context.getString(R.string.csv_header_type)},")
    csvBuilder.append("${context.getString(R.string.csv_header_category)},")
    csvBuilder.append("${context.getString(R.string.csv_header_amount)},")
    csvBuilder.append("${context.getString(R.string.csv_header_date)},")
    csvBuilder.append("${context.getString(R.string.csv_header_description)}\n")

    // Add data rows
    report.transactions?.forEach { item ->
        csvBuilder.append("${item.id},")
        csvBuilder.append("${escapeCsvField(item.type.toString())},")
        csvBuilder.append("${escapeCsvField(item.weight.toString())},")
        csvBuilder.append("${item.itemsCount},")
        csvBuilder.append("${item.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))},")
        csvBuilder.append("${escapeCsvField(item.description)}\n")
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

private fun formatLocalDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return dateTime.format(formatter)
}