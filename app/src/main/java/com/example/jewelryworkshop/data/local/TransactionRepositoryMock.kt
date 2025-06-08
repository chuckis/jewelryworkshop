package com.example.jewelryworkshop.data.local

import com.example.jewelryworkshop.domain.MetalAlloyRepository
import com.jewelryworkshop.app.domain.model.MetalAlloy
import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import com.jewelryworkshop.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

class TransactionRepositoryMock : TransactionRepository, MetalAlloyRepository {
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

    override suspend fun deleteTransaction(transactionId: Long) {}

    override suspend fun updateTransaction(transaction: Transaction) {}

    override suspend fun addAlloy(metalAlloy: MetalAlloy): Long = metalAlloy.id

    override suspend fun deleteAlloy(metalAlloyId: Long) {}

    override suspend fun updateAlloy(metalAlloy: MetalAlloy) {}

    override suspend fun getAlloyById(id: Long): MetalAlloy? {
        TODO("Not yet implemented")
    }

    override fun getAllAlloys(): Flow<List<MetalAlloy>> {
        TODO("Not yet implemented")
    }
}
