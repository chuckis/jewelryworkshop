package com.example.jewelryworkshop.domain

import java.time.LocalDateTime

data class Report(
    val id: Long = 0L,
    val startPeriod: LocalDateTime? = LocalDateTime.now().minusDays(7),
    val endPeriod: LocalDateTime? = LocalDateTime.now(),
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val metalAlloy: MetalAlloy,
    val transactions: List<Transaction>? = emptyList(),
    val createdBy: String? = "",
    val status: ReportStatus? = ReportStatus.GENERATED,
)

enum class ReportStatus {
    GENERATED,
    ARCHIVED,
}
