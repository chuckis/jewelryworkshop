import com.example.jewelryworkshop.data.local.ReportRepositoryInMemory
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.Report
import com.example.jewelryworkshop.domain.ReportStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before
import org.junit.Assert.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


class ReportRepositoryInMemoryTest {

    private lateinit var repository: ReportRepositoryInMemory
    private val metalAlloy = MetalAlloy(1L, "Gold 18K")
    private val startPeriod = LocalDateTime.of(2024, 1, 1, 0, 0)
    private val endPeriod = LocalDateTime.of(2024, 1, 31, 23, 59)

    @Before
    fun setUp() {
        repository = ReportRepositoryInMemory()
    }

    @Test
    fun `should add report and return generated id`() = runBlocking {
        val report = Report(
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            metalAlloy = metalAlloy
        )

        val generatedId = repository.addReport(report).id

        assertTrue(generatedId > 0)
        assertEquals(1L, generatedId)
    }

    @Test
    fun `should generate sequential ids for multiple reports`() = runBlocking {
        val report1 = Report(metalAlloy = metalAlloy)
        val report2 = Report(metalAlloy = metalAlloy)

        val id1 = repository.addReport(report1).id
        val id2 = repository.addReport(report2).id

        assertEquals(1L, id1)
        assertEquals(2L, id2)
    }

    @Test
    fun `should get report by id`() = runBlocking {
        val report = Report(
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            metalAlloy = metalAlloy,
            createdBy = "test-user"
        )

        val addedId = repository.addReport(report).id
        val retrievedReport = repository.getReport(Report(id = addedId, metalAlloy = metalAlloy))

        assertNotNull(retrievedReport)
        assertEquals(addedId, retrievedReport?.id)
        assertEquals(startPeriod, retrievedReport?.startPeriod)
        assertEquals(endPeriod, retrievedReport?.endPeriod)
        assertEquals(metalAlloy, retrievedReport?.metalAlloy)
        assertEquals("test-user", retrievedReport?.createdBy)
    }

    @Test
    fun `should return null when getting non-existent report`() = runBlocking {
        val nonExistentReport = Report(id = 999L, metalAlloy = metalAlloy)

        val result = repository.getReport(nonExistentReport)

        assertNull(result)
    }

    @Test
    fun `should update existing report`() = runBlocking {
        val originalReport = Report(
            metalAlloy = metalAlloy,
            createdBy = "original-user"
        )

        val addedId = repository.addReport(originalReport).id
        val updatedReport = Report(
            id = addedId,
            metalAlloy = metalAlloy,
            createdBy = "updated-user",
            status = ReportStatus.ARCHIVED
        )

        repository.updateReport(updatedReport)
        val retrievedReport = repository.getReport(Report(id = addedId, metalAlloy = metalAlloy))

        assertNotNull(retrievedReport)
        assertEquals("updated-user", retrievedReport?.createdBy)
        assertEquals(ReportStatus.ARCHIVED, retrievedReport?.status)
    }

    @Test
    fun `should delete report`() = runBlocking {
        val report = Report(metalAlloy = metalAlloy)

        val addedId = repository.addReport(report)
        repository.deleteReport(report)
        val retrievedReport = repository.getReport(report)

        assertNull(retrievedReport)
    }

    @Test
    fun `should get all reports after adding multiple`() = runBlocking {
        val report1 = Report(metalAlloy = metalAlloy, createdBy = "user1")
        val report2 = Report(metalAlloy = metalAlloy, createdBy = "user2")

        repository.addReport(report1)
        repository.addReport(report2)
        val allReports = repository.getAllReports()

        assertEquals(2, allReports.size)
        assertEquals("user1", allReports[0].createdBy)
        assertEquals("user2", allReports[1].createdBy)
    }

    @Test
    fun `should maintain correct count after delete operation`() = runBlocking {
        val report1 = Report(metalAlloy = metalAlloy)
        val report2 = Report(metalAlloy = metalAlloy)

        val id1 = repository.addReport(report1).id
        repository.addReport(report2)
        repository.deleteReport(Report(id = id1, metalAlloy = metalAlloy))
        val allReports = repository.getAllReports()

        assertEquals(1, allReports.size)
    }

    @Test
    fun `should not affect other reports when updating one`() = runBlocking {
        val report1 = Report(metalAlloy = metalAlloy, createdBy = "user1")
        val report2 = Report(metalAlloy = metalAlloy, createdBy = "user2")

        val id1 = repository.addReport(report1).id
        val id2 = repository.addReport(report2).id

        val updatedReport1 = Report(id = id1, metalAlloy = metalAlloy, createdBy = "updated-user1")
        repository.updateReport(updatedReport1)

        val retrievedReport1 = repository.getReport(Report(id = id1, metalAlloy = metalAlloy))
        val retrievedReport2 = repository.getReport(Report(id = id2, metalAlloy = metalAlloy))

        assertEquals("updated-user1", retrievedReport1?.createdBy)
        assertEquals("user2", retrievedReport2?.createdBy)
    }

    @Test
    fun `should return 7 days period by default` () = runBlocking {
        val report = Report(metalAlloy = metalAlloy, createdBy = "user1")

        val startDate = report.startPeriod as LocalDateTime
        val endDate = report.endPeriod as LocalDateTime
        val daysCount = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate())

        assertEquals(7, daysCount.toInt())
    }
}