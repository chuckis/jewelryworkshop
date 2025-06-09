package com.example.jewelryworkshop.data.local


import com.example.jewelryworkshop.data.local.entity.MetalAlloyEntity
import com.example.jewelryworkshop.data.local.entity.TransactionEntity
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.MetalBalance
import com.example.jewelryworkshop.domain.Transaction
import kotlinx.coroutines.flow.*


class CombinedRepositoryRoom(
    private val transactionDao: TransactionDao,
    private val metalAlloyDao: MetalAlloyDao
) : CombinedRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        TODO("Not yet implemented")
    }

//    override fun getAllTransactions(): Flow<List<Transaction>> {
//        return transactionDao.getAllTransactionsWithAlloy().map { entities ->
//            entities.map { transactionWithAlloy ->
//                transactionWithAlloy.transaction.toDomain(transactionWithAlloy.alloy)
//            }
//        }
//    }

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



    override suspend fun addAlloy(metalAlloy: MetalAlloy): Long {
        return metalAlloyDao.insert(MetalAlloyEntity.fromDomain(metalAlloy))
    }

    suspend fun deleteMetalAlloy(metalAlloyId: Long) {
        metalAlloyDao.delete(metalAlloyId)
    }

    suspend fun updateMetalAlloy(metalAlloy: MetalAlloy) {
        metalAlloyDao.update(MetalAlloyEntity.fromDomain(metalAlloy))
    }

    override suspend fun getAlloy(alloy: MetalAlloy): MetalAlloy? {
        return metalAlloyDao.getById(alloy.id) as MetalAlloy?
    }

    override fun getAllAlloys(): Flow<List<MetalAlloy>> {
        TODO("Not yet implemented")
    }


    override suspend fun updateAlloy(alloy: MetalAlloy) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlloy(alloyId: Long) {
        TODO("Not yet implemented")
    }
}

