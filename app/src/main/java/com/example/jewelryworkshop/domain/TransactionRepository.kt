package com.jewelryworkshop.app.domain.repository

import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с данными ювелирной мастерской
 */
interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getMetalBalance(): Flow<MetalBalance>
    suspend fun addTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(transactionId: Long)
    suspend fun updateTransaction(transaction: Transaction)
}

enum class RepositoryType {
    ROOM_DATABASE,
    IN_MEMORY_TEST,
    MOCK_DATA // для демо-данных
}