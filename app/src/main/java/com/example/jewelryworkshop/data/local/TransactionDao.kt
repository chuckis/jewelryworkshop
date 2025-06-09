package com.jewelryworkshop.app.data.local.dao

import androidx.room.*
import com.jewelryworkshop.app.data.local.entity.TransactionEntity
import com.jewelryworkshop.app.data.local.entity.TransactionWithAlloy
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с транзакциями в базе данных
 */
@Dao
interface TransactionDao {

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun getAllTransactionsWithAlloy(): Flow<List<TransactionWithAlloy>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE alloyId = :alloyId")
    suspend fun getTransactionsByAlloy(alloyId: Long): List<TransactionWithAlloy>
    /**
     * Получить все транзакции, отсортированные по дате (сначала новые)
     */
//    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
//    fun getAllTransactions(): Flow<List<TransactionEntity>>

    /**
     * Получить общий баланс по весу полученных металлов/изделий
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'RECEIVED' THEN weight ELSE -weight END), 0) FROM transactions")
    fun getTotalWeightBalance(): Flow<Double>

    /**
     * Получить общий баланс по количеству изделий
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'RECEIVED' THEN itemsCount ELSE -itemsCount END), 0) FROM transactions")
    fun getTotalItemsBalance(): Flow<Int?>

    /**
     * Добавить новую транзакцию
     * @return ID добавленной записи
     */
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    /**
     * Обновить существующую транзакцию
     */
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    /**
     * Удалить транзакцию по ID
     */
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Long)

    /**
     * Получить транзакцию по ID
     */
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): TransactionEntity?

}

