package com.jewelryworkshop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jewelryworkshop.app.domain.model.MetalAlloy
import com.jewelryworkshop.app.domain.model.MetalBalance
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * ViewModel для основного экрана приложения
 */
class MainViewModel(private val repository: JewelryRepository) : ViewModel() {

    // Поток всех транзакций, отсортированных по дате
    val transactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Текущий баланс металлов/изделий
    val metalBalance: StateFlow<MetalBalance> = repository.getMetalBalance()
        .stateIn(viewModelScope, SharingStarted.Lazily, MetalBalance(0.0, 0))

    // Поток всех доступных сплавов
    val alloys: StateFlow<List<MetalAlloy>> = repository.getAllAlloys() as StateFlow<List<MetalAlloy>>

    /**
     * Добавить новую транзакцию
     */
    fun addTransaction(
        dateTime: LocalDateTime,
        weight: Double,
        type: TransactionType,
        description: String,
        itemsCount: Int?,
        alloyId: Long,
    ) {
        viewModelScope.launch {
            try {
                // Получаем сплав по ID
                val alloy = repository.getAlloyById(alloyId)
                if (alloy != null) {
                    val transaction = Transaction(
                        id = 0, // Будет автоматически сгенерирован
                        dateTime = dateTime,
                        weight = weight,
                        type = type,
                        description = description,
                        itemsCount = itemsCount,
                        alloy = alloy,
                    )
                    repository.addTransaction(transaction)
                } else {
                    // Обработка ошибки - сплав не найден
                    _errorMessage.value = "Сплав не найден"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при добавлении транзакции: ${e.message}"
            }
        }
    }

    /**
     * Удалить транзакцию
     */
    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transactionId)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при удалении транзакции: ${e.message}"
            }
        }
    }

    /**
     * Обновить существующую транзакцию
     */
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при обновлении транзакции: ${e.message}"
            }
        }
    }

    /**
     * Добавить новый сплав
     */
    fun addAlloy(name: String) {
        viewModelScope.launch {
            try {
                val alloy = MetalAlloy(
                    id = 0, // Будет автоматически сгенерирован
                    name = name
                )
                repository.addAlloy(alloy)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при добавлении сплава: ${e.message}"
            }
        }
    }

    // Приватное поле для ошибок
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Очистить сообщение об ошибке
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
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