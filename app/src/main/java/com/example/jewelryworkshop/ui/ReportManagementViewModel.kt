package com.example.jewelryworkshop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jewelryworkshop.data.local.CombinedRepository
import com.example.jewelryworkshop.data.local.CombinedRepositoryMock
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.Report
import com.example.jewelryworkshop.domain.service.CreateReportService
import com.example.jewelryworkshop.domain.service.ReportSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime


data class ReportManagementUiState(
    val isLoading: Boolean = false,
    val availableAlloys: List<MetalAlloy> = emptyList(),
    val selectedAlloy: MetalAlloy? = null,
    val startPeriod: LocalDateTime = LocalDateTime.now().minusMonths(1),
    val endPeriod: LocalDateTime = LocalDateTime.now(),
    val createdBy: String = "",
    val generatedReports: List<Report> = emptyList(),
    val reportSummary: ReportSummary? = null,
    val errorMessage: String? = null,
    val isGeneratingReport: Boolean = false,
    val isLoadingSummary: Boolean = false
)

class ReportManagementViewModel(
    private val repository: CombinedRepository
) : ViewModel() {

    private val createReportService = CreateReportService(repository)

    private val _uiState = MutableStateFlow(ReportManagementUiState())
    val uiState: StateFlow<ReportManagementUiState> = _uiState.asStateFlow()

    init {
        loadAvailableAlloys()
    }

    fun loadAvailableAlloys() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                val alloys = repository.getAllAlloys().first()
                _uiState.value = _uiState.value.copy(
                    availableAlloys = alloys,
                    selectedAlloy = alloys.firstOrNull(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Ошибка загрузки сплавов: ${e.message}"
                )
            }
        }
    }

    fun selectAlloy(alloy: MetalAlloy?) {
        _uiState.value = _uiState.value.copy(selectedAlloy = alloy)
        // Auto-refresh summary when alloy changes
        if (_uiState.value.reportSummary != null) {
            loadReportSummary()
        }
    }

    fun updateStartPeriod(startPeriod: LocalDateTime) {
        _uiState.value = _uiState.value.copy(startPeriod = startPeriod)
    }

    fun updateEndPeriod(endPeriod: LocalDateTime) {
        _uiState.value = _uiState.value.copy(endPeriod = endPeriod)
    }

    fun updateCreatedBy(createdBy: String) {
        _uiState.value = _uiState.value.copy(createdBy = createdBy)
    }

    fun generateSingleReport() {
        val currentState = _uiState.value
        val selectedAlloy = currentState.selectedAlloy

        if (selectedAlloy == null) {
            _uiState.value = currentState.copy(
                errorMessage = "Select an alloy for Report"
            )
            return
        }

        if (!validatePeriod()) return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isGeneratingReport = true,
                    errorMessage = null
                )

                val report = createReportService.createReport(
                    startPeriod = currentState.startPeriod,
                    endPeriod = currentState.endPeriod,
                    metalAlloy = selectedAlloy,
                    createdBy = currentState.createdBy
                )

                _uiState.value = _uiState.value.copy(
                    generatedReports = listOf(report),
                    isGeneratingReport = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingReport = false,
                    errorMessage = "Error of Report creating: ${e.message}"
                )
            }
        }
    }

    fun generateReportsForAllAlloys() {
        val currentState = _uiState.value

        if (!validatePeriod()) return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isGeneratingReport = true,
                    errorMessage = null
                )

                val reports = createReportService.createReportsGroupedByAlloy(
                    startPeriod = currentState.startPeriod,
                    endPeriod = currentState.endPeriod,
                    createdBy = currentState.createdBy
                )

                _uiState.value = _uiState.value.copy(
                    generatedReports = reports,
                    isGeneratingReport = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingReport = false,
                    errorMessage = "Error of Reports creation: ${e.message}"
                )
            }
        }
    }

    fun loadReportSummary() {
        val currentState = _uiState.value
        val selectedAlloy = currentState.selectedAlloy

        if (selectedAlloy == null) {
            _uiState.value = currentState.copy(
                errorMessage = "Choose Report for summary check"
            )
            return
        }

        if (!validatePeriod()) return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoadingSummary = true,
                    errorMessage = null
                )

                val summary = createReportService.getReportSummary(
                    startPeriod = currentState.startPeriod,
                    endPeriod = currentState.endPeriod,
                    metalAlloy = selectedAlloy
                )

                _uiState.value = _uiState.value.copy(
                    reportSummary = summary,
                    isLoadingSummary = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSummary = false,
                    errorMessage = "Error of summary: ${e.message}"
                )
            }
        }
    }

    fun clearReports() {
        _uiState.value = _uiState.value.copy(
            generatedReports = emptyList(),
            reportSummary = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun setPeriodToLastMonth() {
        val now = LocalDateTime.now()
        _uiState.value = _uiState.value.copy(
            startPeriod = now.minusMonths(1),
            endPeriod = now
        )
    }

    fun setPeriodToLastWeek() {
        val now = LocalDateTime.now()
        _uiState.value = _uiState.value.copy(
            startPeriod = now.minusWeeks(1),
            endPeriod = now
        )
    }

    fun setPeriodToToday() {
        val now = LocalDateTime.now()
        val startOfDay = now.toLocalDate().atStartOfDay()
        val endOfDay = now.toLocalDate().atTime(23, 59, 59)

        _uiState.value = _uiState.value.copy(
            startPeriod = startOfDay,
            endPeriod = endOfDay
        )
    }

    private fun validatePeriod(): Boolean {
        val currentState = _uiState.value
        if (currentState.startPeriod.isAfter(currentState.endPeriod)) {
            _uiState.value = currentState.copy(
                errorMessage = "StartDate can't be later when EndDate"
            )
            return false
        }
        return true
    }
}

class ReportManagementViewModelFactory(
    private val repository: CombinedRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportManagementViewModel::class.java)) {
            return ReportManagementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}