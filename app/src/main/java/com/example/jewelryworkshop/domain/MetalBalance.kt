package com.example.jewelryworkshop.domain

data class MetalBalance(
    val totalWeight: Double,
    val totalItems: Int?,
    val transactions: List<Transaction> = emptyList()
)