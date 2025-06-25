package com.example.jewelryworkshop.domain

import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.DefaultAsserter.assertTrue


class ReportTest {

    @Test
    fun `should create report with all parameters`() {
        val startPeriod = LocalDateTime.of(2024, 1, 1, 0, 0)
        val endPeriod = LocalDateTime.of(2024, 1, 31, 23, 59)
        val metalAlloy = MetalAlloy(1L, "Gold 18K")

        val report = Report(
            id = 123L,
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            metalAlloy = metalAlloy
        )

        assertEquals(123L, report.id)
        assertEquals(startPeriod, report.startPeriod)
        assertEquals(endPeriod, report.endPeriod)
        assertEquals(metalAlloy, report.metalAlloy)
    }

    @Test
    fun `should create report with default id`() {
        val startPeriod = LocalDateTime.of(2024, 1, 1, 0, 0)
        val endPeriod = LocalDateTime.of(2024, 1, 31, 23, 59)
        val metalAlloy = MetalAlloy(2L, "Silver 925")

        val report = Report(
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            metalAlloy = metalAlloy
        )

        assertEquals(0L, report.id)
    }

    @Test
    fun `should create report with default endPeriod`() {
        val startPeriod = LocalDateTime.of(2024, 1, 1, 0, 0)
        val metalAlloy = MetalAlloy(3L, "Platinum")
        val beforeCreation = LocalDateTime.now()

        val report = Report(
            startPeriod = startPeriod,
            metalAlloy = metalAlloy
        )

        val afterCreation = LocalDateTime.now()

        assertEquals(0L, report.id)
        assertEquals(startPeriod, report.startPeriod)
        assertEquals(metalAlloy, report.metalAlloy)
        assertTrue("EndPeriod should be close to now",
            report.endPeriod?.isAfter(beforeCreation) == true || report.endPeriod?.isEqual(beforeCreation) == true
        )
        assertTrue("EndPeriod should be close to now",
            report.endPeriod?.isBefore(afterCreation) == true || report.endPeriod?.isEqual(afterCreation) == true
        )
    }

    @Test
    fun `should create report with both default id and endPeriod`() {
        val startPeriod = LocalDateTime.of(2024, 6, 1, 12, 0)
        val metalAlloy = MetalAlloy(4L, "Gold 24K")
        val beforeCreation = LocalDateTime.now()

        val report = Report(
            startPeriod = startPeriod,
            metalAlloy = metalAlloy
        )

        val afterCreation = LocalDateTime.now()

        assertEquals(0L, report.id)
        assertEquals(startPeriod, report.startPeriod)
        assertEquals(metalAlloy, report.metalAlloy)
        assertTrue("EndPeriod should be close to now",
            report.endPeriod?.isAfter(beforeCreation) == true || report.endPeriod?.isEqual(beforeCreation) == true
        )
        assertTrue("EndPeriod should be close to now",
            report.endPeriod?.isBefore(afterCreation) == true || report.endPeriod?.isEqual(afterCreation) == true
        )
    }

    @Test
    fun `should handle same startPeriod and endPeriod`() {
        val dateTime = LocalDateTime.of(2025, 3, 15, 10, 30)
        val metalAlloy = MetalAlloy(5L, "Copper")

        val report = Report(
            startPeriod = dateTime,
            endPeriod = dateTime,
            metalAlloy = metalAlloy
        )

        assertEquals(dateTime, report.startPeriod)
        assertEquals(dateTime, report.endPeriod)
    }

    @Test
    fun `should handle endPeriod before startPeriod`() {
        val startPeriod = LocalDateTime.of(2025, 6, 1, 12, 0)
        val endPeriod = LocalDateTime.of(2025, 5, 1, 12, 0)
        val metalAlloy = MetalAlloy(6L, "Bronze")

        val report = Report(
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            metalAlloy = metalAlloy
        )

        assertEquals(startPeriod, report.startPeriod)
        assertEquals(endPeriod, report.endPeriod)
        assertTrue("EndPeriod is before startPeriod", report.endPeriod!!.isBefore(report.startPeriod))
    }

    @Test
    fun `should support copy with modifications`() {
        val originalStart = LocalDateTime.of(2024, 1, 1, 0, 0)
        val originalEnd = LocalDateTime.of(2024, 1, 31, 23, 59)
        val originalAlloy = MetalAlloy(1L, "Gold")
        val newAlloy = MetalAlloy(2L, "Silver")

        val original = Report(
            id = 1L,
            startPeriod = originalStart,
            endPeriod = originalEnd,
            metalAlloy = originalAlloy
        )

        val modified = original.copy(
            id = 2L,
            metalAlloy = newAlloy
        )

        assertEquals(2L, modified.id)
        assertEquals(originalStart, modified.startPeriod)
        assertEquals(originalEnd, modified.endPeriod)
        assertEquals(newAlloy, modified.metalAlloy)
    }

    @Test
    fun `should implement equals and hashCode correctly`() {
        val startPeriod = LocalDateTime.of(2024, 1, 1, 0, 0)
        val endPeriod = LocalDateTime.of(2024, 1, 31, 23, 59)
        val metalAlloy = MetalAlloy(1L, "Gold")

        val report1 = Report(
            id = 1L,
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            metalAlloy = metalAlloy
        )

        val report2 = Report(
            id = 1L,
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            metalAlloy = metalAlloy
        )

        val report3 = report1.copy(id = 2L)

        assertEquals(report1, report2)
        assertEquals(report1.hashCode(), report2.hashCode())
        assertNotEquals(report1, report3)
    }

    @Test
    fun `should handle different metalAlloy types`() {
        val startPeriod = LocalDateTime.of(2024, 1, 1, 0, 0)
        val endPeriod = LocalDateTime.of(2024, 1, 31, 23, 59)

        val goldAlloy = MetalAlloy(1L, "Gold 18K")
        val silverAlloy = MetalAlloy(2L, "Silver 925")
        val platinumAlloy = MetalAlloy(3L, "Platinum 950")

        val goldReport = Report(startPeriod = startPeriod, endPeriod = endPeriod, metalAlloy = goldAlloy)
        val silverReport = Report(startPeriod = startPeriod, endPeriod = endPeriod, metalAlloy = silverAlloy)
        val platinumReport = Report(startPeriod = startPeriod, endPeriod = endPeriod, metalAlloy = platinumAlloy)

        assertEquals(goldAlloy, goldReport.metalAlloy)
        assertEquals(silverAlloy, silverReport.metalAlloy)
        assertEquals(platinumAlloy, platinumReport.metalAlloy)
        assertNotEquals(goldReport, silverReport)
        assertNotEquals(silverReport, platinumReport)
    }
}