package com.example.jewelryworkshop.data.local

import com.example.jewelryworkshop.domain.Report
import com.example.jewelryworkshop.domain.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong

class ReportRepositoryInMemory : ReportRepository {
    private val idGenerator = AtomicLong(1L)
    private val _reports = MutableStateFlow<List<Report>>(emptyList())

    override fun addReport(report: Report): Long {
        val newId = idGenerator.getAndIncrement()
        val reportWithId = report.copy(id = newId)

        _reports.value = _reports.value + reportWithId
        return newId
    }

    override suspend fun updateReport(report: Report) {
        _reports.value = _reports.value.map { existingReport ->
            if (existingReport.id == report.id) {
                report
            } else {
                existingReport
            }
        }
    }

    override suspend fun deleteReport(report: Report) {
        _reports.value = _reports.value.filter { it.id != report.id }
    }

    override suspend fun getReport(report: Report): Report? {
        return _reports.value.find { it.id == report.id }
    }

    override fun getAllReports(): Flow<List<Report>> {
        return _reports.asStateFlow()
    }
}