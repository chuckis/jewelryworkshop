package com.jewelryworkshop.app.data.repository


import com.example.jewelryworkshop.data.local.MetalAlloyDao
import com.example.jewelryworkshop.data.local.MetalAlloyEntity
import com.jewelryworkshop.app.data.local.dao.TransactionDao
import com.jewelryworkshop.app.data.local.entity.TransactionEntity
import com.jewelryworkshop.app.domain.model.MetalAlloy
import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import kotlinx.coroutines.flow.*


class JewelryRepositoryRoom(
    private val transactionDao: TransactionDao,
    private val metalAlloyDao: MetalAlloyDao
) : JewelryRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { entity ->
                val alloy = metalAlloyDao.getById(entity.alloyId)
                entity.toDomain(
                    alloy = TODO()
                )
            }
        }
    }

    override fun getMetalBalance(): Flow<MetalBalance> {
        return combine(
            transactionDao.getTotalWeightBalance(),
            transactionDao.getTotalItemsBalance()
        ) { weightBalance, itemsBalance ->
            MetalBalance(
                totalWeight = weightBalance,
                totalItems = itemsBalance ?: 0
            )
        }
    }

    override suspend fun addTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(TransactionEntity.fromDomain(transaction))
    }

    override suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.deleteTransaction(transactionId)
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(TransactionEntity.fromDomain(transaction))
    }



    override suspend fun addMetalAlloy(metalAlloy: MetalAlloy): Long {
        return metalAlloyDao.insert(MetalAlloyEntity.fromDomain(metalAlloy))
    }

    override suspend fun deleteMetalAlloy(metalAlloyId: Long) {
        metalAlloyDao.delete(metalAlloyId)
    }

    override suspend fun updateMetalAlloy(metalAlloy: MetalAlloy) {
        metalAlloyDao.update(MetalAlloyEntity.fromDomain(metalAlloy))
    }

    override suspend fun getAlloyById(id: Long): MetalAlloy? {
        return metalAlloyDao.getById(id)
    }

    override fun getAllAlloys(): List<MetalAlloy> {
        return metalAlloyDao.getAllAlloys()
    }

    override suspend fun addAlloy(alloy: MetalAlloy) {
        TODO("Not yet implemented")
    }
}

