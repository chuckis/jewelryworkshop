import android.util.Log
import com.example.jewelryworkshop.data.local.CombinedRepositoryMock
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.ReportStatus
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import com.example.jewelryworkshop.domain.service.CreateReportService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before
import org.junit.Assert.*
import java.time.LocalDateTime

class CreateReportServiceTest {

    private lateinit var combinedRepositoryMock: CombinedRepositoryMock
    private lateinit var createReportService: CreateReportService

    // Using the same alloys as in the mock
    private val goldAlloy = MetalAlloy(1, "Золото")
    private val silverAlloy = MetalAlloy(2, "Серебро")
    private val platinumAlloy = MetalAlloy(3, "Платина")

    private val baseDateTime = LocalDateTime.now()

    @Before
    fun setUp() {
        combinedRepositoryMock = CombinedRepositoryMock()
        createReportService = CreateReportService(combinedRepositoryMock)
    }

    // ===== CreateReport Tests =====

    @Test
    fun `should create report with filtered transactions by date and alloy`() = runBlocking {
        // Given
        val startPeriod = baseDateTime.minusDays(7)
        val endPeriod = baseDateTime.plusDays(7)

        // When
        val result = createReportService.createReport(startPeriod, endPeriod, goldAlloy, "testUser")

        // Then
        assertEquals(goldAlloy, result.metalAlloy)
        assertEquals("testUser", result.createdBy)
        assertEquals(ReportStatus.GENERATED, result.status)
        assertEquals(startPeriod, result.startPeriod)
        assertEquals(endPeriod, result.endPeriod)

        // Should contain only gold transactions within date range
        val goldTransactions = result.transactions?.filter { it.alloy.id == goldAlloy.id }
        assertEquals(2, goldTransactions?.size) // Based on mock data: 2 gold transactions
        assertTrue(result.transactions?.all {
            it.dateTime >= startPeriod && it.dateTime <= endPeriod && it.alloy.id == goldAlloy.id
        } ?: false)
    }

    @Test
    fun `should set default createdBy when not provided`() = runBlocking {
        // Given
        val startPeriod = baseDateTime.minusDays(1)
        val endPeriod = baseDateTime.plusDays(1)

        // When
        val result = createReportService.createReport(startPeriod, endPeriod, silverAlloy)

        // Then
        assertEquals("", result.createdBy)
        assertEquals(silverAlloy, result.metalAlloy)
        assertEquals(ReportStatus.GENERATED, result.status)
    }

    @Test
    fun `should create report with no transactions when alloy has no data in period`() = runBlocking {
        // Given - using a date range that might not include platinum transactions
        val startPeriod = baseDateTime.plusDays(10)
        val endPeriod = baseDateTime.plusDays(20)

        // When
        val result = createReportService.createReport(startPeriod, endPeriod, platinumAlloy)

        // Then
        assertEquals(platinumAlloy, result.metalAlloy)
        assertEquals(ReportStatus.GENERATED, result.status)
        // Should have empty or filtered transactions list
        assertTrue(result.transactions?.isEmpty() ?: true)
    }

    // ===== CreateReportsGroupedByAlloy Tests =====

    @Test
    fun `should create separate reports for each alloy with transactions`() = runBlocking {
        // Given
        val startPeriod = baseDateTime.minusDays(7)
        val endPeriod = baseDateTime.plusDays(7)

        // When
        val results = createReportService.createReportsGroupedByAlloy(startPeriod, endPeriod, "testUser")

        // Then
        assertTrue(results.isNotEmpty())

        // Each report should have the same date range and creator
        results.forEach { report ->
            assertEquals(startPeriod, report.startPeriod)
            assertEquals(endPeriod, report.endPeriod)
            assertEquals("testUser", report.createdBy)
            assertEquals(ReportStatus.GENERATED, report.status)

            // All transactions in each report should be of the same alloy
            val alloyId = report.metalAlloy.id
            assertTrue(report.transactions?.all { it.alloy.id == alloyId } ?: true)
        }

        // Should have reports for alloys that have transactions in the date range
        val alloyIds = results.map { it.metalAlloy.id }.toSet()
        assertTrue(alloyIds.contains(goldAlloy.id)) // Gold should be present
        assertTrue(alloyIds.contains(silverAlloy.id)) // Silver should be present
    }

    @Test
    fun `should return empty list when no transactions in future date range`() = runBlocking {
        // Given - far future date range
        val startPeriod = baseDateTime.plusDays(365)
        val endPeriod = baseDateTime.plusDays(400)

        // When
        val results = createReportService.createReportsGroupedByAlloy(startPeriod, endPeriod)

        // Then
        assertTrue(results.isEmpty())
    }

    // ===== Calculation Tests =====

    @Test
    fun `should calculate weights correctly for gold transactions from mock data`() = runBlocking {
        // Given
        val startPeriod = baseDateTime.minusDays(7)
        val endPeriod = baseDateTime.plusDays(7)

        // When
        val summary = createReportService.getReportSummary(startPeriod, endPeriod, goldAlloy)

        // Then
        assertEquals(goldAlloy, summary.metalAlloy)
        assertEquals(startPeriod, summary.startPeriod)
        assertEquals(endPeriod, summary.endPeriod)

        // Based on mock data:
        // - Transaction 1: 100.0 RECEIVED, 5 items
        // - Transaction 2: 50.0 ISSUED, 2 items
        val calc = summary.calculation
        assertEquals(100.0, calc.totalReceivedWeight, 0.001)
        assertEquals(50.0, calc.totalIssuedWeight, 0.001)
        assertEquals(50.0, calc.netWeight, 0.001) // 100 - 50
        assertEquals(5, calc.totalReceivedItems)
        assertEquals(2, calc.totalIssuedItems)
        assertEquals(3, calc.netItems) // 5 - 2
        assertEquals(2, calc.transactionCount)
    }

    @Test
    fun `should calculate weights correctly for silver transactions from mock data`() = runBlocking {
        // Given
        val startPeriod = baseDateTime.minusDays(7)
        val endPeriod = baseDateTime.plusDays(7)

        // When
        val summary = createReportService.getReportSummary(startPeriod, endPeriod, silverAlloy)

        // Then
        assertEquals(silverAlloy, summary.metalAlloy)

        // Based on mock data:
        // - Transaction 3: 200.0 RECEIVED, 10 items (silver)
        val calc = summary.calculation
        assertEquals(200.0, calc.totalReceivedWeight, 0.001)
        assertEquals(0.0, calc.totalIssuedWeight, 0.001)
        assertEquals(200.0, calc.netWeight, 0.001)
        assertEquals(10, calc.totalReceivedItems)
        assertEquals(0, calc.totalIssuedItems)
        assertEquals(10, calc.netItems)
        assertEquals(1, calc.transactionCount)
    }

    @Test
    fun `should return zero calculations when no transactions match criteria`() = runBlocking {
        // Given - future date range
        val startPeriod = baseDateTime.plusDays(100)
        val endPeriod = baseDateTime.plusDays(200)

        // When
        val summary = createReportService.getReportSummary(startPeriod, endPeriod, goldAlloy)

        // Then
        val calc = summary.calculation
        assertEquals(0.0, calc.totalReceivedWeight, 0.001)
        assertEquals(0.0, calc.totalIssuedWeight, 0.001)
        assertEquals(0.0, calc.netWeight, 0.001)
        assertEquals(0, calc.totalReceivedItems)
        assertEquals(0, calc.totalIssuedItems)
        assertEquals(0, calc.netItems)
        assertEquals(0, calc.transactionCount)
    }

    // ===== Integration Tests =====

    @Test
    fun `should work with actual mock data structure`() = runBlocking {
        // Given
        val allTransactions = combinedRepositoryMock.getAllTransactions().first()

        // Then - verify mock data structure
        assertEquals(3, allTransactions.size)

        // Verify gold transactions
        val goldTransactions = allTransactions.filter { it.alloy.id == goldAlloy.id }
        assertEquals(2, goldTransactions.size)

        // Verify silver transactions
        val silverTransactions = allTransactions.filter { it.alloy.id == silverAlloy.id }
        assertEquals(1, silverTransactions.size)

        // Verify transaction types
        assertTrue("Should have RECEIVED transaction",
            allTransactions.any { it.type == TransactionType.RECEIVED })
        assertTrue("Should have ISSUED transaction",
            allTransactions.any { it.type == TransactionType.ISSUED })
    }

    @Test
    fun `should handle repository flow correctly`() = runBlocking {
        // Given
        val startPeriod = baseDateTime.minusDays(7)
        val endPeriod = baseDateTime.plusDays(7)

        // When - call multiple times to ensure Flow works correctly
        val result1 = createReportService.createReport(startPeriod, endPeriod, goldAlloy)
        val result2 = createReportService.createReport(startPeriod, endPeriod, silverAlloy)

        // Then
        assertNotNull(result1)
        assertNotNull(result2)
        assertEquals(goldAlloy.id, result1.metalAlloy.id)
        assertEquals(silverAlloy.id, result2.metalAlloy.id)
    }

    // ===== Helper Methods =====

    private fun assumeNotNull(message: String, obj: Any?) {
        if (obj == null) {
            throw AssumptionViolatedException(message)
        }
    }
}

// Custom exception for test assumptions
class AssumptionViolatedException(message: String) : Exception(message)