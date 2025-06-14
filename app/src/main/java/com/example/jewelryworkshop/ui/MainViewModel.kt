package com.example.jewelryworkshop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jewelryworkshop.data.local.CombinedRepository
import com.example.jewelryworkshop.domain.MetalAlloy
import com.example.jewelryworkshop.domain.MetalBalance
import com.example.jewelryworkshop.domain.Transaction
import com.example.jewelryworkshop.domain.TransactionType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Data class для отображения сплавов с количеством транзакций
 */
data class AlloyWithCount(
    val alloy: MetalAlloy,
    val transactionCount: Int,
    val totalWeight: Double
)

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
    val alloys: StateFlow<List<MetalAlloy>> = repository.getAllAlloys()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Транзакции, сгруппированные по сплавам
    val transactionsByAlloy: StateFlow<Map<MetalAlloy, List<Transaction>>> =
        transactions.map { transactionsList ->
            transactionsList
                .filter { it.alloy != null } // Фильтруем транзакции без сплава
                .groupBy { it.alloy!! }
                .toSortedMap(compareBy { it.name }) // Сортируем по имени сплава
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Список сплавов с количеством транзакций (для отображения во вкладках)
    val alloysWithCounts: StateFlow<List<AlloyWithCount>> =
        transactionsByAlloy.map { grouped ->
            grouped.map { (alloy, transactions) ->
                AlloyWithCount(
                    alloy = alloy,
                    transactionCount = transactions.size,
                    totalWeight = transactions.sumOf { it.weight }
                )
            }.sortedBy { it.alloy.name }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Приватное поле для ошибок
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Добавить новую транзакцию
     */
    fun addTransaction(
        dateTime: LocalDateTime,
        weight: Double,
        type: TransactionType,
        description: String,
        itemsCount: Int?,
        alloyId: Long, // Изменено: принимаем ID сплава вместо объекта
    ) {
        viewModelScope.launch {
            try {
                // Получаем сплав по ID
                val selectedAlloy = alloys.value.find { it.id == alloyId }
                if (selectedAlloy != null) {
                    val transaction = Transaction(
                        id = 0, // Будет автоматически сгенерирован
                        dateTime = dateTime,
                        weight = weight,
                        type = type,
                        description = description,
                        itemsCount = itemsCount,
                        alloy = selectedAlloy,
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
    fun updateTransaction(
        transactionId: Long,
        dateTime: LocalDateTime,
        weight: Double,
        type: TransactionType,
        description: String,
        itemsCount: Int?,
        alloyId: Long, // Принимаем ID сплава вместо объекта
    ) {
        viewModelScope.launch {
            try {
                // Получаем сплав по ID
                val selectedAlloy = alloys.value.find { it.id == alloyId }
                if (selectedAlloy != null) {
                    val updatedTransaction = Transaction(
                        id = transactionId, // Используем переданный ID для обновления
                        dateTime = dateTime,
                        weight = weight,
                        type = type,
                        description = description,
                        itemsCount = itemsCount,
                        alloy = selectedAlloy,
                    )
                    repository.updateTransaction(updatedTransaction)
                } else {
                    // Обработка ошибки - сплав не найден
                    _errorMessage.value = "Сплав не найден"
                }
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

    /**
     * Удалить сплав
     */
    fun deleteAlloy(alloy: MetalAlloy) {
        viewModelScope.launch {
            try {
                repository.deleteAlloy(alloy)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при удалении сплава: ${e.message}"
            }
        }
    }

    /**
     * Обновить сплав
     */
    fun updateAlloy(alloy: MetalAlloy) {
        viewModelScope.launch {
            try {
                repository.updateAlloy(alloy)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при обновлении сплава: ${e.message}"
            }
        }
    }

    /**
     * Получить транзакции для конкретного сплава
     */
    fun getTransactionsForAlloy(alloyId: Long): StateFlow<List<Transaction>> {
        return transactionsByAlloy.map { grouped ->
            grouped.entries.find { it.key.id == alloyId }?.value ?: emptyList()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

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