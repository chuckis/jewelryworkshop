package com.example.jewelryworkshop.domain

import java.math.BigDecimal

data class JewelryItem(
    val id: Long = 0L,
    val name: String,
    val type: JewelryType,
    val alloy: MetalAlloy,
    val weight: Double,
    val description: String? = null,
    val estimatedValue: BigDecimal? = null,
    val serialNumber: String? = null,
    val photos: List<String>? = emptyList(),
) {

}

enum class JewelryType {
    RING,
    BRACELET,
    EARRINGS,
    NECKLACE,
    OTHER,
}
