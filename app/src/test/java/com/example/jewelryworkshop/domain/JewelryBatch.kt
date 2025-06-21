package com.example.jewelryworkshop.domain

import java.math.BigDecimal

data class JewelryBatch(
    val id: Long = 0L,
    val name: String? = null,
    val description: String? = null,
    val items: List<JewelryItem>? = null,
) {

    val itemsCount: Long
        get() = items?.size?.toLong() ?: 0L

    val totalEstimatedValue: BigDecimal
        get() = items?.mapNotNull { it.estimatedValue }
            ?.fold(BigDecimal.ZERO) { acc, value -> acc + value }
            ?: BigDecimal.ZERO

    val totalWeight: Double
        get() = items?.sumOf { it.weight } ?: 0.0
}