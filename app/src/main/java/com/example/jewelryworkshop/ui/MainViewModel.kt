package com.example.jewelryworkshop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jewelryworkshop.data.local.CombinedRepository
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.MetalBalance
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
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
class MainViewModel(private val repository: CombinedRepository) : ViewModel() {

    // Поток всех транзакций, отсортированных по дате
    val transactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Текущий баланс металлов/изделий
    val metalBalance: StateFlow<MetalBalance> = repository.getMetalBalance()
        .stateIn(viewModelScope, SharingStarted.Lazily, MetalBalance(0.0, 0))

    // Поток всех доступных сплавов
    val alloys: StateFlow<List<MetalAlloy>> =repository.getAllAlloys().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    /**
     * Добавить новую транзакцию
     */
    fun addTransaction(
        dateTime: LocalDateTime,
        weight: Double,
        type: TransactionType,
        description: String,
        itemsCount: Int?,
        alloy: MetalAlloy,
    ) {
        viewModelScope.launch {
            try {
                // Получаем сплав
                val alloy = alloys.value.find { it.id == alloy.id }
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
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
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
    class Factory(private val repository: CombinedRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    repository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}