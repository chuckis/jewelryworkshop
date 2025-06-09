package com.example.jewelryworkshop.data.local

import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.MetalBalance
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

class CombinedRepositoryMock : CombinedRepository {
    private val mockAlloys = listOf(
        MetalAlloy(1, "Золото"),
        MetalAlloy(2, "Серебро"),
        MetalAlloy(3, "Платина")
    )

    private val mockTransactions = listOf(
        Transaction(1, LocalDateTime.now(), 100.0, TransactionType.RECEIVED, "Получен лом золота", 5, mockAlloys[0]),
        Transaction(2, LocalDateTime.now().minusDays(1), 50.0, TransactionType.ISSUED, "Выдано на переплавку", 2, mockAlloys[0]),
        Transaction(3, LocalDateTime.now().minusDays(2), 200.0, TransactionType.RECEIVED, "Получено серебро", 10, mockAlloys[1]),
    )

    override fun getAllTransactions(): Flow<List<Transaction>> = flowOf(mockTransactions)

    override fun getMetalBalance(): Flow<MetalBalance> = flowOf(
        MetalBalance(totalWeight = 250.0, totalItems = 13)
    )

    override suspend fun addTransaction(transaction: Transaction): Long = transaction.id

    override suspend fun deleteTransaction(transaction: Transaction) {}

    override suspend fun updateTransaction(transaction: Transaction) {}

    override suspend fun addAlloy(metalAlloy: MetalAlloy): Long = metalAlloy.id

    override suspend fun deleteAlloy(metalAlloy: MetalAlloy) {}

    override suspend fun updateAlloy(metalAlloy: MetalAlloy) {}

    override suspend fun getAlloy(alloy: MetalAlloy): MetalAlloy {
        TODO("Not yet implemented")
    }

    override fun getAllAlloys(): Flow<List<MetalAlloy>> {
        TODO("Not yet implemented")
    }
}
