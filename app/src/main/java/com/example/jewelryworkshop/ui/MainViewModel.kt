package com.jewelryworkshop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * ViewModel для основного экрана приложения
 */
class MainViewModel(private val repository: JewelryRepository) : ViewModel() {

    // Поток всех транзакций, отсортированных по дате
    val transactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    // Текущий баланс металлов/изделий
    val metalBalance: StateFlow<MetalBalance> = repository.getMetalBalance()
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, MetalBalance(0.0, 0))

    /**
     * Добавить новую транзакцию
     */
    fun addTransaction(
        dateTime: LocalDateTime,
        weight: Double,
        type: TransactionType,
        description: String,
        itemsCount: Int
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                dateTime = dateTime,
                weight = weight,
                type = type,
                description = description,
                itemsCount = itemsCount
            )
            repository.addTransaction(transaction)
        }
    }

    /**
     * Удалить транзакцию
     */
    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
        }
    }

    /**
     * Обновить существующую транзакцию
     */
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    /**
     * Factory для создания MainViewModel с внедрением зависимостей
     */
    class Factory(private val repository: JewelryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}