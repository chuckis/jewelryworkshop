package com.jewelryworkshop.app.domain.repository

//import com.example.jewelryworkshop.domain.TransactionRepository
import com.jewelryworkshop.app.domain.model.MetalAlloy
import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlin.unaryMinus

/**
 * Интерфейс репозитория для работы с данными ювелирной мастерской
 */
interface JewelryRepository {
    /**
     * Получить поток всех транзакций, отсортированных по дате (от новых к старым)
     */
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Получить текущий баланс металлов/изделий
     */
    fun getMetalBalance(): Flow<MetalBalance>

    /**
     * Добавить новую транзакцию
     * @param transaction новая транзакция
     * @return ID добавленной транзакции
     */
    suspend fun addTransaction(transaction: Transaction): Long

    /**
     * Удалить транзакцию по ID
     * @param transactionId ID транзакции
     */
    suspend fun deleteTransaction(transactionId: Long)

    /**
     * Обновить существующую транзакцию
     * @param transaction обновленная транзакция
     */
    suspend fun updateTransaction(transaction: Transaction)

    suspend fun addMetalAlloy(metalAlloy: MetalAlloy): Long

    suspend fun deleteMetalAlloy(metalAlloyId: Long)

    suspend fun updateMetalAlloy(metalAlloy: MetalAlloy)

}

enum class RepositoryType {
    ROOM_DATABASE,
    IN_MEMORY_TEST,
    MOCK_DATA // для демо-данных
}