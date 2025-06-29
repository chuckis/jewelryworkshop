package com.example.jewelryworkshop.data.local

import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.MetalBalance
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CombinedRepositoryInMemory : CombinedRepository {
    private val transactions = mutableListOf<Transaction>()
    private val metalAlloys = mutableListOf<MetalAlloy>()
    private var nextTransactionId = 1L
    private var nextAlloyId = 1L

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return flowOf(transactions.sortedByDescending { it.dateTime })
    }

    override fun getMetalBalance(): Flow<MetalBalance> {
        val totalWeight = transactions.sumOf { transaction ->
            when (transaction.type) {
                TransactionType.RECEIVED -> transaction.weight
                TransactionType.ISSUED -> -transaction.weight
            }
        }

        val totalItems = transactions.sumOf { transaction ->
            val items = transaction.itemsCount ?: 0
            when (transaction.type) {
                TransactionType.RECEIVED -> items
                TransactionType.ISSUED -> -items
            }
        }

        return flowOf(MetalBalance(totalWeight, totalItems))
    }

    override suspend fun addTransaction(transaction: Transaction): Long {
        val newTransaction = transaction.copy(id = nextTransactionId++)
        transactions.add(newTransaction)
        return newTransaction.id
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactions.removeAll { it.id == transaction.id }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            transactions[index] = transaction
        }
    }

    override suspend fun addAlloy(metalAlloy: MetalAlloy): Long {
        val newAlloy = metalAlloy.copy(id = nextAlloyId++)
        metalAlloys.add(newAlloy)
        return newAlloy.id
    }

    override suspend fun deleteAlloy(metalAlloy: MetalAlloy) {
        metalAlloys.removeAll { it.id == metalAlloy.id }
        // Также удаляем связанные транзакции
        transactions.removeAll { it.alloy.id == metalAlloy.id }
    }

    override suspend fun updateAlloy(metalAlloy: MetalAlloy) {
        val index = metalAlloys.indexOfFirst { it.id == metalAlloy.id }
        if (index != -1) {
            metalAlloys[index] = metalAlloy
            // Обновляем ссылки в транзакциях
            transactions.forEachIndexed { transactionIndex, transaction ->
                if (transaction.alloy.id == metalAlloy.id) {
                    transactions[transactionIndex] = transaction.copy(alloy = metalAlloy)
                }
            }
        }
    }

    override suspend fun getAlloy(metalAlloy: MetalAlloy): MetalAlloy {
        val index = metalAlloys.indexOfFirst { it.id == metalAlloy.id }
        return metalAlloys[index]
    }

    override fun getAllAlloys(): Flow<List<MetalAlloy>> {
        return flowOf(metalAlloys)
    }
}