package com.example.jewelryworkshop.domain

import java.time.LocalDateTime

enum class TransactionType {
    RECEIVED,
    ISSUED
}


data class Transaction(
    val id: Long = 0,
    val dateTime: LocalDateTime,
    val weight: Double,
    val type: TransactionType,
    val description: String,
    val itemsCount: Int?,
    val alloy: MetalAlloy,
)