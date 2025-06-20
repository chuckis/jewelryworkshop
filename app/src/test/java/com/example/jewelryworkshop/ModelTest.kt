package com.example.jewelryworkshop

import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.MetalBalance
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class TransactionTest {

    @Test
    fun `should create transaction with all parameters`() {
        val dateTime = LocalDateTime.now()
        val alloy = MetalAlloy(1L, "Gold 18K")

        val transaction = Transaction(
            id = 123L,
            dateTime = dateTime,
            weight = 25.5,
            type = TransactionType.RECEIVED,
            description = "Received gold from supplier",
            itemsCount = 10,
            alloy = alloy
        )

        assertEquals(123L, transaction.id)
        assertEquals(dateTime, transaction.dateTime)
        assertEquals(25.5, transaction.weight, 0.001)
        assertEquals(TransactionType.RECEIVED, transaction.type)
        assertEquals("Received gold from supplier", transaction.description)
        assertEquals(10, transaction.itemsCount)
        assertEquals(alloy, transaction.alloy)
    }

    @Test
    fun `should create transaction with default id`() {
        val dateTime = LocalDateTime.now()
        val alloy = MetalAlloy(1L, "Silver 925")

        val transaction = Transaction(
            dateTime = dateTime,
            weight = 15.0,
            type = TransactionType.ISSUED,
            description = "Issued silver for production",
            itemsCount = null,
            alloy = alloy
        )

        assertEquals(0L, transaction.id)
    }

    @Test
    fun `should handle null itemsCount`() {
        val dateTime = LocalDateTime.now()
        val alloy = MetalAlloy(2L, "Platinum")

        val transaction = Transaction(
            dateTime = dateTime,
            weight = 5.75,
            type = TransactionType.RECEIVED,
            description = "Platinum ingot",
            itemsCount = null,
            alloy = alloy
        )

        assertNull(transaction.itemsCount)
    }

    @Test
    fun `should handle zero weight`() {
        val dateTime = LocalDateTime.now()
        val alloy = MetalAlloy(1L, "Gold")

        val transaction = Transaction(
            dateTime = dateTime,
            weight = 0.0,
            type = TransactionType.ISSUED,
            description = "Zero weight transaction",
            itemsCount = 0,
            alloy = alloy
        )

        assertEquals(0.0, transaction.weight, 0.001)
    }

    @Test
    fun `should handle negative weight`() {
        val dateTime = LocalDateTime.now()
        val alloy = MetalAlloy(1L, "Gold")

        val transaction = Transaction(
            dateTime = dateTime,
            weight = -10.5,
            type = TransactionType.ISSUED,
            description = "Adjustment transaction",
            itemsCount = null,
            alloy = alloy
        )

        assertEquals(-10.5, transaction.weight, 0.001)
    }

    @Test
    fun `should support copy with modifications`() {
        val originalDateTime = LocalDateTime.now()
        val alloy = MetalAlloy(1L, "Gold")

        val original = Transaction(
            id = 1L,
            dateTime = originalDateTime,
            weight = 10.0,
            type = TransactionType.RECEIVED,
            description = "Original",
            itemsCount = 5,
            alloy = alloy
        )

        val modified = original.copy(
            weight = 20.0,
            description = "Modified"
        )

        assertEquals(1L, modified.id)
        assertEquals(originalDateTime, modified.dateTime)
        assertEquals(20.0, modified.weight, 0.001)
        assertEquals(TransactionType.RECEIVED, modified.type)
        assertEquals("Modified", modified.description)
        assertEquals(5, modified.itemsCount)
        assertEquals(alloy, modified.alloy)
    }

    @Test
    fun `should implement equals and hashCode correctly`() {
        val dateTime = LocalDateTime.now()
        val alloy = MetalAlloy(1L, "Gold")

        val transaction1 = Transaction(
            id = 1L,
            dateTime = dateTime,
            weight = 10.0,
            type = TransactionType.RECEIVED,
            description = "Test",
            itemsCount = 5,
            alloy = alloy
        )

        val transaction2 = Transaction(
            id = 1L,
            dateTime = dateTime,
            weight = 10.0,
            type = TransactionType.RECEIVED,
            description = "Test",
            itemsCount = 5,
            alloy = alloy
        )

        val transaction3 = transaction1.copy(id = 2L)

        assertEquals(transaction1, transaction2)
        assertEquals(transaction1.hashCode(), transaction2.hashCode())
        assertNotEquals(transaction1, transaction3)
    }
}

class TransactionTypeTest {

    @Test
    fun `should have correct enum values`() {
        val values = TransactionType.entries.toTypedArray()

        assertEquals(2, values.size)
        assertTrue(values.contains(TransactionType.RECEIVED))
        assertTrue(values.contains(TransactionType.ISSUED))
    }

    @Test
    fun `should convert from string`() {
        assertEquals(TransactionType.RECEIVED, TransactionType.valueOf("RECEIVED"))
        assertEquals(TransactionType.ISSUED, TransactionType.valueOf("ISSUED"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for invalid enum value`() {
        TransactionType.valueOf("INVALID")
    }

    @Test
    fun `should have correct string representation`() {
        assertEquals("RECEIVED", TransactionType.RECEIVED.toString())
        assertEquals("ISSUED", TransactionType.ISSUED.toString())
    }
}

class MetalBalanceTest {

    @Test
    fun `should create balance with all parameters`() {
        val alloy = MetalAlloy(1L, "Gold")
        val transactions = listOf(
            Transaction(
                id = 1L,
                dateTime = LocalDateTime.now(),
                weight = 10.0,
                type = TransactionType.RECEIVED,
                description = "Test",
                itemsCount = 5,
                alloy = alloy
            )
        )

        val balance = MetalBalance(
            totalWeight = 25.5,
            totalItems = 15,
            transactions = transactions
        )

        assertEquals(25.5, balance.totalWeight, 0.001)
        assertEquals(15, balance.totalItems)
        assertEquals(transactions, balance.transactions)
        assertEquals(1, balance.transactions.size)
    }

    @Test
    fun `should create balance with empty transactions list by default`() {
        val balance = MetalBalance(
            totalWeight = 100.0,
            totalItems = null
        )

        assertEquals(100.0, balance.totalWeight, 0.001)
        assertNull(balance.totalItems)
        assertTrue(balance.transactions.isEmpty())
    }

    @Test
    fun `should handle null totalItems`() {
        val balance = MetalBalance(
            totalWeight = 50.0,
            totalItems = null,
            transactions = emptyList()
        )

        assertNull(balance.totalItems)
    }

    @Test
    fun `should handle zero values`() {
        val balance = MetalBalance(
            totalWeight = 0.0,
            totalItems = 0,
            transactions = emptyList()
        )

        assertEquals(0.0, balance.totalWeight, 0.001)
        assertEquals(0, balance.totalItems)
    }

    @Test
    fun `should handle negative values`() {
        val balance = MetalBalance(
            totalWeight = -10.5,
            totalItems = -5,
            transactions = emptyList()
        )

        assertEquals(-10.5, balance.totalWeight, 0.001)
        assertEquals(-5, balance.totalItems)
    }

    @Test
    fun `should support copy with modifications`() {
        val originalTransactions = listOf<Transaction>()
        val original = MetalBalance(
            totalWeight = 10.0,
            totalItems = 5,
            transactions = originalTransactions
        )

        val newTransactions = listOf(
            Transaction(
                dateTime = LocalDateTime.now(),
                weight = 5.0,
                type = TransactionType.RECEIVED,
                description = "New",
                itemsCount = 1,
                alloy = MetalAlloy(1L, "Gold")
            )
        )

        val modified = original.copy(
            totalWeight = 15.0,
            transactions = newTransactions
        )

        assertEquals(15.0, modified.totalWeight, 0.001)
        assertEquals(5, modified.totalItems)
        assertEquals(newTransactions, modified.transactions)
    }

    @Test
    fun `should implement equals and hashCode correctly`() {
        val transactions = listOf<Transaction>()

        val balance1 = MetalBalance(
            totalWeight = 10.0,
            totalItems = 5,
            transactions = transactions
        )

        val balance2 = MetalBalance(
            totalWeight = 10.0,
            totalItems = 5,
            transactions = transactions
        )

        val balance3 = balance1.copy(totalWeight = 20.0)

        assertEquals(balance1, balance2)
        assertEquals(balance1.hashCode(), balance2.hashCode())
        assertNotEquals(balance1, balance3)
    }
}

class MetalAlloyTest {

    @Test
    fun `should create alloy with all parameters`() {
        val alloy = MetalAlloy(
            id = 123L,
            name = "Gold 18K"
        )

        assertEquals(123L, alloy.id)
        assertEquals("Gold 18K", alloy.name)
    }

    @Test
    fun `should handle empty name`() {
        val alloy = MetalAlloy(
            id = 1L,
            name = ""
        )

        assertEquals("", alloy.name)
    }

    @Test
    fun `should handle special characters in name`() {
        val alloy = MetalAlloy(
            id = 1L,
            name = "Silver 925 (99.9% pure) - Premium Grade"
        )

        assertEquals("Silver 925 (99.9% pure) - Premium Grade", alloy.name)
    }

    @Test
    fun `should handle zero and negative ids`() {
        val alloy1 = MetalAlloy(id = 0L, name = "Test")
        val alloy2 = MetalAlloy(id = -1L, name = "Test")

        assertEquals(0L, alloy1.id)
        assertEquals(-1L, alloy2.id)
    }

    @Test
    fun `should support copy with modifications`() {
        val original = MetalAlloy(
            id = 1L,
            name = "Original"
        )

        val modified = original.copy(name = "Modified")

        assertEquals(1L, modified.id)
        assertEquals("Modified", modified.name)
    }

    @Test
    fun `should implement equals and hashCode correctly`() {
        val alloy1 = MetalAlloy(id = 1L, name = "Gold")
        val alloy2 = MetalAlloy(id = 1L, name = "Gold")
        val alloy3 = MetalAlloy(id = 2L, name = "Gold")
        val alloy4 = MetalAlloy(id = 1L, name = "Silver")

        assertEquals(alloy1, alloy2)
        assertEquals(alloy1.hashCode(), alloy2.hashCode())
        assertNotEquals(alloy1, alloy3)
        assertNotEquals(alloy1, alloy4)
    }

    @Test
    fun `should have meaningful toString representation`() {
        val alloy = MetalAlloy(id = 1L, name = "Gold 18K")
        val toString = alloy.toString()

        assertTrue(toString.contains("1"))
        assertTrue(toString.contains("Gold 18K"))
    }
}