package com.example.jewelryworkshop.domain

import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с данными ювелирной мастерской
 */
interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getMetalBalance(): Flow<MetalBalance>
    suspend fun addTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
}

enum class RepositoryType {
    ROOM_DATABASE,
    IN_MEMORY_TEST,
    MOCK_DATA // для демо-данных
}