package com.jewelryworkshop.app.data.repository

import com.jewelryworkshop.app.data.local.dao.TransactionDao
import com.jewelryworkshop.app.data.local.entity.TransactionEntity
import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import kotlinx.coroutines.flow.*

/**
 * Реализация репозитория для работы с данными ювелирной мастерской
 */
class JewelryRepositoryImpl(private val transactionDao: TransactionDao) : JewelryRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsWithAlloy().map { it.toDomain()
        } as Flow<List<Transaction>>
    }


    override fun getMetalBalance(): Flow<MetalBalance> {
        return combine(
            transactionDao.getTotalWeightBalance(),
            transactionDao.getTotalItemsBalance(),
            getAllTransactions()
        ) { totalWeight, totalItems, transactions ->
            MetalBalance(
                totalWeight = totalWeight,
                totalItems = totalItems,
                transactions = transactions
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

}