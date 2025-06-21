package com.example.jewelryworkshop

import com.example.jewelryworkshop.domain.*
import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal
import java.time.LocalDateTime


class CustomerTest {

    @Test
    fun `should create customer with all parameters`() {
        val createdAt = LocalDateTime.now()
        val customer = Customer(
            id = 1L,
            firstName = "John",
            lastName = "Smith",
            phoneNumber = "+1-555-123-4567",
            email = "john.smith@example.com",
            address = "123 Main Street, Springfield",
            notes = "Regular customer",
            createdAt = createdAt
        )

        assertEquals(1L, customer.id)
        assertEquals("John", customer.firstName)
        assertEquals("Smith", customer.lastName)
        assertEquals("+1-555-123-4567", customer.phoneNumber)
        assertEquals("john.smith@example.com", customer.email)
        assertEquals("123 Main Street, Springfield", customer.address)
        assertEquals("Regular customer", customer.notes)
        assertEquals(createdAt, customer.createdAt)
    }

    @Test
    fun `should create customer with minimal parameters`() {
        val specificDateTime = LocalDateTime.of(2023, 10, 15, 12, 30)
        val customer = Customer(
            firstName = "Anna",
            lastName = "Johnson",
            phoneNumber = "+1-555-987-6543",
            createdAt = specificDateTime,
        )

        assertEquals(0L, customer.id)
        assertEquals("Anna", customer.firstName)
        assertEquals("Johnson", customer.lastName)
        assertEquals("+1-555-987-6543", customer.phoneNumber)
        assertNull(customer.email)
        assertNull(customer.address)
        assertNull(customer.notes)
        assertEquals("Anna Johnson", customer.fullName)
        assertEquals(specificDateTime, customer.createdAt)
    }
}

class JewelryItemTest {

    @Test
    fun `should create jewelry item with all parameters`() {
        val alloy = MetalAlloy(1L, "Gold 18K")
        val photos = listOf("photo1.jpg", "photo2.jpg")

        val item = JewelryItem(
            id = 1L,
            name = "Wedding Ring",
            type = JewelryType.RING,
            alloy = alloy,
            weight = 5.5,
            description = "Classic wedding ring",
            estimatedValue = BigDecimal("50000"),
            serialNumber = "RG2024001",
            photos = photos
        )

        assertEquals(1L, item.id)
        assertEquals("Wedding Ring", item.name)
        assertEquals(JewelryType.RING, item.type)
        assertEquals(alloy, item.alloy)
        assertEquals(5.5, item.weight, 0.001)
        assertEquals("Classic wedding ring", item.description)
        assertEquals(BigDecimal("50000"), item.estimatedValue)
        assertEquals("RG2024001", item.serialNumber)
        assertEquals(photos, item.photos)
    }

    @Test
    fun `should create jewelry item with minimal parameters`() {
        val alloy = MetalAlloy(1L, "Silver 925")

        val item = JewelryItem(
            name = "Earrings",
            type = JewelryType.EARRINGS,
            alloy = alloy,
            weight = 3.2,
        )

        assertEquals(0L, item.id)
        assertEquals("Earrings", item.name)
        assertEquals(JewelryType.EARRINGS, item.type)
        assertEquals(alloy, item.alloy)
        assertEquals(3.2, item.weight, 0.001)
        assertNull(item.description)
        assertNull(item.estimatedValue)
        assertNull(item.serialNumber)
        assertTrue(item.photos?.isEmpty() == true)
    }
}

class JewelryBatchTest {

    @Test
    fun `should create batch with items and calculate totals`() {
        val alloy = MetalAlloy(1L, "Gold 18K")
        val items = listOf(
            JewelryItem(
                name = "Ring 1",
                type = JewelryType.RING,
                alloy = alloy,
                weight = 5.0,
                estimatedValue = BigDecimal("30000")
            ),
            JewelryItem(
                name = "Ring 2",
                type = JewelryType.RING,
                alloy = alloy,
                weight = 6.5,
                estimatedValue = BigDecimal("40000")
            )
        )

        val batch = JewelryBatch(
            id = 1L,
            name = "Wedding Rings Batch",
            description = "Wedding order",
            items = items
        )

        assertEquals(1L, batch.id)
        assertEquals("Wedding Rings Batch", batch.name)
        assertEquals("Wedding order", batch.description)
        assertEquals(items, batch.items)
        assertEquals(11.5, batch.totalWeight, 0.001)
        assertEquals(BigDecimal("70000"), batch.totalEstimatedValue)
        assertEquals(2, batch.itemsCount)
    }

    @Test
    fun `should create empty batch`() {
        val batch = JewelryBatch(
            name = "Empty Batch"
        )

        assertEquals(0L, batch.id)
        assertEquals("Empty Batch", batch.name)
        assertNull(batch.description)
        assertTrue(batch.items?.isEmpty() == true)
        assertEquals(0.0, batch.totalWeight, 0.001)
        assertEquals(BigDecimal.ZERO, batch.totalEstimatedValue)
        assertEquals(0, batch.itemsCount)
    }

    @Test
    fun `should handle items without estimated value`() {
        val alloy = MetalAlloy(1L, "Silver")
        val items = listOf(
            JewelryItem(
                name = "Bracelet without estimate",
                type = JewelryType.BRACELET,
                alloy = alloy,
                weight = 10.0,
                estimatedValue = null
            )
        )

        val batch = JewelryBatch(
            name = "Batch with unvalued items",
            items = items
        )

        assertEquals(BigDecimal.ZERO, batch.totalEstimatedValue)
        assertEquals(10.0, batch.totalWeight, 0.001)
    }
}

class RepairOrderTest {

    @Test
    fun `should create repair order with all parameters`() {
        val customer = Customer(
            id = 1L,
            firstName = "John",
            lastName = "Smith",
            phoneNumber = "+1-555-123-4567"
        )

        val alloy = MetalAlloy(1L, "Gold 18K")
        val item = JewelryItem(
            name = "Ring",
            type = JewelryType.RING,
            alloy = alloy,
            weight = 5.0
        )

        val service = RepairService(
            type = RepairType.POLISHING,
            description = "Ring polishing",
            estimatedPrice = BigDecimal("2000"),
            estimatedDurationHours = 2
        )

        val estimatedDate = LocalDateTime.now().plusDays(7)
        val order = RepairOrder(
            id = 1L,
            orderNumber = "ORD-2024-001",
            customer = customer,
            items = listOf(item),
            services = listOf(service),
            status = OrderStatus.CONFIRMED,
            priority = OrderPriority.HIGH,
            description = "Wedding ring polishing",
            estimatedCompletionDate = estimatedDate,
            totalEstimatedPrice = BigDecimal("2000"),
            depositAmount = BigDecimal("1000"),
            depositPaid = true
        )

        assertEquals(1L, order.id)
        assertEquals("ORD-2024-001", order.orderNumber)
        assertEquals(customer, order.customer)
        assertEquals(1, order.items!!.size)
        assertEquals(item, order.items!![0])
        assertEquals(1, order.services!!.size)
        assertEquals(service, order.services[0])
        assertEquals(OrderStatus.CONFIRMED, order.status)
        assertEquals(OrderPriority.HIGH, order.priority)
        assertEquals("Wedding ring polishing", order.description)
        assertEquals(estimatedDate, order.estimatedCompletionDate)
        assertEquals(BigDecimal("2000"), order.totalEstimatedPrice)
        assertEquals(BigDecimal("1000"), order.depositAmount)
        assertTrue(order.depositPaid == true)
        assertTrue(order.finalPaymentReceived)

        assertEquals(1, order.totalItemsCount)
        assertEquals(5.0, order.totalWeight, 0.001)
        assertFalse(order.isCompleted)
        assertFalse(order.isInProgress)
        assertEquals(BigDecimal("1000"), order.remainingBalance)
    }

    @Test
    fun `should calculate totals with batches`() {
        val customer = Customer(
            firstName = "Test",
            lastName = "Client",
            phoneNumber = "123"
        )

        val alloy = MetalAlloy(1L, "Silver")
        val item1 = JewelryItem(name = "Ring", type = JewelryType.RING, alloy = alloy, weight = 3.0)
        val item2 =
            JewelryItem(name = "Earrings", type = JewelryType.EARRINGS, alloy = alloy, weight = 2.0)

        val batch = JewelryBatch(
            name = "Batch",
            items = listOf(
                JewelryItem(
                    name = "Bracelet",
                    type = JewelryType.BRACELET,
                    alloy = alloy,
                    weight = 8.0
                )
            )
        )

        val order = RepairOrder(
            orderNumber = "TEST-001",
            customer = customer,
            items = listOf(item1, item2),
            batches = listOf(batch)
        )

        assertEquals(3, order.totalItemsCount)
        assertEquals(13.0, order.totalWeight, 0.001) // 3 + 2 + 8
    }

    @Test
    fun `should check status properties correctly`() {
        val customer = Customer(firstName = "Test", lastName = "Client", phoneNumber = "123")

        val inProgressOrder = RepairOrder(
            orderNumber = "IP-001",
            customer = customer,
            status = OrderStatus.IN_PROGRESS
        )

        val completedOrder = RepairOrder(
            orderNumber = "COM-001",
            customer = customer,
            status = OrderStatus.COMPLETED
        )

        val waitingOrder = RepairOrder(
            orderNumber = "WAIT-001",
            customer = customer,
            status = OrderStatus.WAITING_PARTS
        )

        assertTrue(inProgressOrder.isInProgress)
        assertFalse(inProgressOrder.isCompleted)

        assertFalse(completedOrder.isInProgress)
        assertTrue(completedOrder.isCompleted)

        assertTrue(waitingOrder.isInProgress)
        assertFalse(waitingOrder.isCompleted)
    }

}

class RepairServiceTest {

    @Test
    fun `should create repair service with all parameters`() {

        // If we need an ERP system, then we need a Material entity with cost, spendings etc.
        val materials = listOf("Polishing paste", "Abrasive wheels")

        val service = RepairService(
            id = 1L,
            type = RepairType.POLISHING,
            description = "Professional mirror-finish polishing",
            estimatedPrice = BigDecimal("1500"),
            estimatedDurationHours = 3,
            notes = "Requires special care",
            materialsNeeded = materials
        )

        assertEquals(1L, service.id)
        assertEquals(RepairType.POLISHING, service.type)
        assertEquals("Professional mirror-finish polishing", service.description)
        assertEquals(BigDecimal("1500"), service.estimatedPrice)
        assertEquals(3, service.estimatedDurationHours)
        assertEquals("Requires special care", service.notes)
        assertEquals(materials, service.materialsNeeded)
    }
}

class EnumTest {

    @Test
    fun `should have all jewelry types`() {
        val types = JewelryType.entries

        assertTrue(types.contains(JewelryType.RING))
        assertTrue(types.contains(JewelryType.EARRINGS))
        assertTrue(types.contains(JewelryType.NECKLACE))
        assertTrue(types.contains(JewelryType.BRACELET))
        assertTrue(types.contains(JewelryType.OTHER))
    }

    @Test
    fun `should have all repair types`() {
        val types = RepairType.entries

        assertTrue(types.contains(RepairType.CLEANING))
        assertTrue(types.contains(RepairType.POLISHING))
        assertTrue(types.contains(RepairType.SIZING))
        assertTrue(types.contains(RepairType.STONE_SETTING))
        assertTrue(types.contains(RepairType.OTHER))
    }

    @Test
    fun `should have all order statuses`() {
        val statuses = OrderStatus.entries

        assertTrue(statuses.contains(OrderStatus.DRAFT))
        assertTrue(statuses.contains(OrderStatus.CONFIRMED))
        assertTrue(statuses.contains(OrderStatus.IN_PROGRESS))
        assertTrue(statuses.contains(OrderStatus.COMPLETED))
        assertTrue(statuses.contains(OrderStatus.DELIVERED))
        assertTrue(statuses.contains(OrderStatus.CANCELLED))
    }

    @Test
    fun `should have all order priorities`() {
        val priorities = OrderPriority.entries

        assertTrue(priorities.contains(OrderPriority.LOW))
        assertTrue(priorities.contains(OrderPriority.NORMAL))
        assertTrue(priorities.contains(OrderPriority.HIGH))
        assertTrue(priorities.contains(OrderPriority.URGENT))
    }
}