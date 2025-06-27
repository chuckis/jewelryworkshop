package com.example.jewelryworkshop.domain.service

import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.Report
import com.example.jewelryworkshop.domain.ReportStatus
import com.example.jewelryworkshop.domain.TransactionRepository
import com.example.jewelryworkshop.domain.TransactionType
import java.time.LocalDateTime

// Very-very OOP-styled
class CreateReportService(
    private val transactionRepository: TransactionRepository
) {

    fun createReport(startPeriod: LocalDateTime,
                     endPeriod: LocalDateTime,
                     metalAlloy: MetalAlloy,
                     createdBy: String? = ""
    ): Report {
        // Get all transactions from repository
        val allTransactions = transactionRepository.getAllTransactions() as List<Transaction>

        // Filter transactions by date range and metal alloy
        val filteredTransactions = allTransactions.filter { transaction ->
            transaction.dateTime >= startPeriod &&
                    transaction.dateTime <= endPeriod &&
                    transaction.alloy == metalAlloy
        }

        // Calculate totals
        val calculationResult = calculateWeightsAndItems(filteredTransactions)

        return Report(
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            createdAt = LocalDateTime.now(),
            metalAlloy = metalAlloy,
            transactions = filteredTransactions,
            createdBy = createdBy,
            status = ReportStatus.GENERATED
        )
    }

    fun createReportsGroupedByAlloy(
        startPeriod: LocalDateTime,
        endPeriod: LocalDateTime,
        createdBy: String = ""
    ): List<Report> {
        // Get all transactions from repository
        val allTransactions = transactionRepository.getAllTransactions() as List<Transaction>

        // Filter transactions by date range
        val filteredTransactions = allTransactions.filter { transaction ->
            transaction.dateTime >= startPeriod &&
                    transaction.dateTime <= endPeriod
        }

        // Group transactions by metal alloy
        val groupedByAlloy = filteredTransactions.groupBy { it.alloy }

        // Create a report for each metal alloy group
        return groupedByAlloy.map { (metalAlloy, transactions) ->
            val calculationResult = calculateWeightsAndItems(transactions)

            Report(
                startPeriod = startPeriod,
                endPeriod = endPeriod,
                createdAt = LocalDateTime.now(),
                metalAlloy = metalAlloy,
                transactions = transactions,
                createdBy = createdBy,
                status = ReportStatus.GENERATED
            )
        }
    }

    private fun calculateWeightsAndItems(transactions: List<Transaction>): TransactionCalculation {
        val receivedTransactions = transactions.filter { it.type == TransactionType.RECEIVED }
        val issuedTransactions = transactions.filter { it.type == TransactionType.ISSUED }

        val totalReceivedWeight = receivedTransactions.sumOf { it.weight }
        val totalIssuedWeight = issuedTransactions.sumOf { it.weight }
        val netWeight = totalReceivedWeight - totalIssuedWeight

        val totalReceivedItems = receivedTransactions.sumOf { it.itemsCount ?: 0 }
        val totalIssuedItems = issuedTransactions.sumOf { it.itemsCount ?: 0 }
        val netItems = totalReceivedItems - totalIssuedItems

        return TransactionCalculation(
            totalReceivedWeight = totalReceivedWeight,
            totalIssuedWeight = totalIssuedWeight,
            netWeight = netWeight,
            totalReceivedItems = totalReceivedItems,
            totalIssuedItems = totalIssuedItems,
            netItems = netItems,
            transactionCount = transactions.size
        )
    }

    fun getReportSummary(
        startPeriod: LocalDateTime,
        endPeriod: LocalDateTime,
        metalAlloy: MetalAlloy
    ): ReportSummary {
        val allTransactions = transactionRepository.getAllTransactions() as List<Transaction>

        val filteredTransactions = allTransactions.filter { transaction ->
            transaction.dateTime >= startPeriod &&
                    transaction.dateTime <= endPeriod &&
                    transaction.alloy.id == metalAlloy.id
        }

        val calculation = calculateWeightsAndItems(filteredTransactions)

        return ReportSummary(
            metalAlloy = metalAlloy,
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            calculation = calculation
        )
    }
}

// Data classes for calculations and summaries
data class TransactionCalculation(
    val totalReceivedWeight: Double,
    val totalIssuedWeight: Double,
    val netWeight: Double,
    val totalReceivedItems: Int,
    val totalIssuedItems: Int,
    val netItems: Int,
    val transactionCount: Int
)

data class ReportSummary(
    val metalAlloy: MetalAlloy,
    val startPeriod: LocalDateTime,
    val endPeriod: LocalDateTime,
    val calculation: TransactionCalculation
)
