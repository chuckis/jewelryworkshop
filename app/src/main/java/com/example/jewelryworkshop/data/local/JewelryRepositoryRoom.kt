package com.jewelryworkshop.app.data.repository

import com.example.jewelryworkshop.domain.MetalBalance
import com.jewelryworkshop.app.data.local.dao.TransactionDao
import com.jewelryworkshop.app.data.local.entity.TransactionEntity
import com.jewelryworkshop.app.domain.model.MetalAlloy
import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import kotlinx.coroutines.flow.*

/**
 * Реализация репозитория для работы с данными ювелирной мастерской
 */
//class JewelryRepositoryImpl(private val transactionDao: TransactionDao) : JewelryRepository {
//
//    override fun getAllTransactions(): Flow<List<Transaction>> {
//        return transactionDao.getAllTransactions().map { entity ->
//            entity.map {it.toDomain()}
//        }
//    }
//
//
//    override fun getMetalBalance(): Flow<MetalBalance> {
//        return combine(
//            transactionDao.getTotalWeightBalance(),
//            transactionDao.getTotalItemsBalance(),
//            getAllTransactions()
//        ) { totalWeight, totalItems, transactions ->
//            MetalBalance(
//                totalWeight = totalWeight,
//                totalItems = totalItems,
//                transactions = transactions
//            )
//        }
//    }
//
//    override suspend fun addTransaction(transaction: Transaction): Long {
//        return transactionDao.insertTransaction(TransactionEntity.fromDomain(transaction))
//    }
//
//    override suspend fun deleteTransaction(transactionId: Long) {
//        transactionDao.deleteTransaction(transactionId)
//    }
//
//    override suspend fun updateTransaction(transaction: Transaction) {
//        transactionDao.updateTransaction(TransactionEntity.fromDomain(transaction))
//    }
//
//}

class JewelryRepositoryRoom(
    private val transactionDao: TransactionDao,
    private val metalAlloyDao: MetalAlloyDao
) : JewelryRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { entity ->
                val alloy = metalAlloyDao.getAlloyById(entity.alloyId)
                entity.toDomain(alloy)
            }
        }
    }

    override fun getMetalBalance(): Flow<com.example.jewelryworkshop.domain.MetalBalance> {
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
        TODO("Not yet implemented")
    }

    override suspend fun addMetalAlloy(metalAlloy: MetalAlloy): Long {
        return metalAlloyDao.insertMetalAlloy(MetalAlloyEntity.fromDomain(metalAlloy))
    }

    override suspend fun deleteMetalAlloy(metalAlloyId: Long) {
        metalAlloyDao.deleteMetalAlloy(metalAlloyId)
    }

    override suspend fun updateMetalAlloy(metalAlloy: MetalAlloy) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMetalAlloy(metalAlloy: MetalAlloy) {
        metalAlloyDao.updateMetalAlloy(MetalAlloyEntity.fromDomain(metalAlloy))
    }
}
