package com.example.jewelryworkshop.domain

import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun addReport(report: Report): Report
    suspend fun updateReport(report: Report)
    suspend fun deleteReport(report: Report)
    suspend fun getReport(report: Report): Report?
}
