package com.example.jewelryworkshop.domain

import java.math.BigDecimal

data class RepairService(
    val id: Long = 0L,
    val type: RepairType,
    val description: String,
    val estimatedPrice: BigDecimal,
    val estimatedDurationHours: Int,
    val notes: String? = null,
    val materialsNeeded: List<String>? = null,
) {

}

enum class RepairType{
    POLISHING,
    CLEANING,
    SIZING,
    STONE_SETTING,
    WELDING,
    OTHER,
}