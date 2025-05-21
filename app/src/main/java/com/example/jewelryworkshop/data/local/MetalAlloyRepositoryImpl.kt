package com.example.jewelryworkshop.data.local

import MetalAlloyDao
import com.example.jewelryworkshop.domain.MetalAlloyRepository
import com.jewelryworkshop.app.domain.model.MetalAlloy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MetalAlloyRepositoryImpl(private val metalAlloyDao: MetalAlloyDao) : MetalAlloyRepository{

    override fun getAllMetalAlloys(): Flow<List<MetalAlloy>> {
        return metalAlloyDao.getAllMetalAlloys().map { entities ->
            entities.map { it.toMetalAlloy() }
        }
    }

    override suspend fun getMetalAlloyById(id: Long): MetalAlloy? {
        return metalAlloyDao.getMetalAlloyById(id)?.toMetalAlloy()
    }

    override suspend fun insertMetalAlloy(metalAlloy: MetalAlloy): Long {
        return metalAlloyDao.insertMetalAlloy(MetalAlloyEntity.fromMetalAlloy(metalAlloy))
    }

    override suspend fun insertMetalAlloys(metalAlloys: List<MetalAlloy>): List<Long> {
        return metalAlloyDao.insertMetalAlloys(
            metalAlloys.map { MetalAlloyEntity.fromMetalAlloy(it) }
        )
    }

    override suspend fun updateMetalAlloy(metalAlloy: MetalAlloy) {
        metalAlloyDao.updateMetalAlloy(MetalAlloyEntity.fromMetalAlloy(metalAlloy))
    }

    override suspend fun deleteMetalAlloy(metalAlloy: MetalAlloy) {
        metalAlloyDao.deleteMetalAlloy(MetalAlloyEntity.fromMetalAlloy(metalAlloy))
    }

    override suspend fun deleteMetalAlloyById(id: Long) {
        metalAlloyDao.deleteMetalAlloyById(id)
    }

    override suspend fun deleteAllMetalAlloys() {
        metalAlloyDao.deleteAllMetalAlloys()
    }
}