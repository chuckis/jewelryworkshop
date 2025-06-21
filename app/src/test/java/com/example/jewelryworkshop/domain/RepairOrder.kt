package com.example.jewelryworkshop.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class RepairOrder(
    val id: Long = 0L,
    val orderNumber: String,
    val customer: Customer,
    val items: List<JewelryItem>? = null,
    val batches: List<JewelryBatch>? = null,
    val services: List<RepairService>? = null,
    val status: OrderStatus? = null,
    val priority: OrderPriority? = null,
    val description: String? = null,
    val estimatedCompletionDate: LocalDateTime? = null,
    val totalEstimatedPrice: BigDecimal? = null,
    val depositAmount: BigDecimal? = null,
    val depositPaid: Boolean? = null,
) {
    val totalItemsCount: Int
        get() = (items?.size ?: 0) + (batches?.sumOf { it.items!!.size } ?: 0)

    val totalWeight: Double
        get() = (items?.sumOf { it.weight } ?: 0.0) + (batches?.sumOf { it.totalWeight } ?: 0.0)

    val isCompleted: Boolean
        get() = status == OrderStatus.COMPLETED

    val isInProgress: Boolean
        get() = status == OrderStatus.IN_PROGRESS

    val remainingBalance: BigDecimal
        get() = totalEstimatedPrice!! - depositAmount!!

    val finalPaymentReceived: Boolean
        get() {
            return depositPaid!!
        }
}

enum class OrderStatus{
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    WAITING_PARTS,
    DRAFT,
    DELIVERED,
    CANCELLED,
}

enum class OrderPriority{
    HIGH,
    LOW,
    NORMAL,
    URGENT,
}