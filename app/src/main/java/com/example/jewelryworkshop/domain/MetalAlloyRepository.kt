package com.example.jewelryworkshop.domain

import com.jewelryworkshop.app.domain.model.MetalAlloy
import kotlinx.coroutines.flow.Flow

interface MetalAlloyRepository {

    fun getAllMetalAlloys(): Flow<List<MetalAlloy>>

    suspend fun getMetalAlloyById(id: Long): MetalAlloy?

    suspend fun insertMetalAlloy(metalAlloy: MetalAlloy): Long

    suspend fun insertMetalAlloys(metalAlloys: List<MetalAlloy>): List<Long>

    suspend fun updateMetalAlloy(metalAlloy: MetalAlloy)

    suspend fun deleteMetalAlloy(metalAlloy: MetalAlloy)

    suspend fun deleteMetalAlloyById(id: Long)

    suspend fun deleteAllMetalAlloys()
}