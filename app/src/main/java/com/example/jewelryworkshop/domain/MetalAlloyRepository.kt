package com.example.jewelryworkshop.domain

import kotlinx.coroutines.flow.Flow

interface MetalAlloyRepository {
    suspend fun addAlloy(alloy: MetalAlloy): Long
    suspend fun updateAlloy(alloy: MetalAlloy)
    suspend fun deleteAlloy(alloy: MetalAlloy)
    suspend fun getAlloy(alloy: MetalAlloy): Any
    fun getAllAlloys(): Flow<List<MetalAlloy>>
}