package com.example.jewelryworkshop.data.local

import com.example.jewelryworkshop.domain.Report
import com.example.jewelryworkshop.domain.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong

class ReportRepositoryInMemory : ReportRepository {

    private val data = mutableListOf<Report>()

    override fun addReport(report: Report): Report {
        val nextId = (data.maxOfOrNull { it.id ?: 0L } ?: 0L) + 1
        val reportWithId = report.copy(id = nextId)
        data.add(reportWithId)
        return reportWithId
    }

    /**
     * Updates an existing report in the data collection.
     *
     * Finds the report with the matching ID and replaces it with the provided report.
     * If no matching report is found, no changes are made to the collection.
     *
     * @param report The updated report to replace the existing one
     */
    override suspend fun updateReport(report: Report) {
        val existingIndex = data.indexOfFirst { it.id == report.id }

        if (existingIndex >= 0) {
            data[existingIndex] = report
        }
    }

    /**
     * Deletes a report from the data collection.
     *
     * Removes the report with the matching ID from the collection.
     * If no matching report is found, no changes are made to the collection.
     *
     * @param report The report to delete (only the ID is used for matching)
     */
    override suspend fun deleteReport(report: Report) {
        data.removeIf { it.id == report.id }
    }

    /**
     * Retrieves a report from the data collection by ID.
     *
     * @param report The report containing the ID to search for
     * @return The matching report if found, null otherwise
     */
    override suspend fun getReport(report: Report): Report? {
        return data.find { it.id == report.id }
    }

    suspend fun getAllReports(): List<Report> {
        return data
    }

    fun reset() {
        data.clear()
    }
}