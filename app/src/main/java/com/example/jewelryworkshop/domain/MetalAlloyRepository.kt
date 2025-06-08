package com.example.jewelryworkshop.domain

import com.jewelryworkshop.app.domain.model.MetalAlloy
import kotlinx.coroutines.flow.Flow

interface MetalAlloyRepository {
    suspend fun addAlloy(alloy: MetalAlloy): Long
    suspend fun updateAlloy(alloy: MetalAlloy)
    suspend fun deleteAlloy(alloyId: Long)
    suspend fun getAlloyById(id: Long): MetalAlloy?
    fun getAllAlloys(): Flow<List<MetalAlloy>>
}