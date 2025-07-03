package com.example.jewelryworkshop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jewelryworkshop.data.local.CombinedRepository
import com.example.jewelryworkshop.domain.MetalAlloy
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
 * Main ViewModel
 */
class MainViewModel(private val repository: CombinedRepository) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val alloys: StateFlow<List<MetalAlloy>> = repository.getAllAlloys()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactionsByAlloy: StateFlow<Map<MetalAlloy, List<Transaction>>> =
        transactions.map { transactionsList ->
            transactionsList
                .filter { true }
                .groupBy { it.alloy }
                .toSortedMap(compareBy { it.name })
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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

                val selectedAlloy = alloys.value.find { it.id == alloyId }
                if (selectedAlloy != null) {
                    val transaction = Transaction(
                        id = 0,
                        dateTime = dateTime,
                        weight = weight,
                        type = type,
                        description = description,
                        itemsCount = itemsCount,
                        alloy = selectedAlloy,
                    )
                    repository.addTransaction(transaction)
                } else {

                    _errorMessage.value = "Alloy not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error while add transaction: ${e.message}"
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
            } catch (e: Exception) {
                _errorMessage.value = "Error while delete transaction: ${e.message}"
            }
        }
    }

    fun updateTransaction(
        transactionId: Long,
        dateTime: LocalDateTime,
        weight: Double,
        type: TransactionType,
        description: String,
        itemsCount: Int?,
        alloyId: Long,
    ) {
        viewModelScope.launch {
            try {
                val selectedAlloy = alloys.value.find { it.id == alloyId }
                if (selectedAlloy != null) {
                    val updatedTransaction = Transaction(
                        id = transactionId,
                        dateTime = dateTime,
                        weight = weight,
                        type = type,
                        description = description,
                        itemsCount = itemsCount,
                        alloy = selectedAlloy,
                    )
                    repository.updateTransaction(updatedTransaction)
                } else {
                    _errorMessage.value = "Alloy not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error while update transaction: ${e.message}"
            }
        }
    }

    fun addAlloy(name: String) {
        viewModelScope.launch {
            try {
                val alloy = MetalAlloy(
                    id = 0,
                    name = name
                )
                repository.addAlloy(alloy)
            } catch (e: Exception) {
                _errorMessage.value = "Error while add alloy: ${e.message}"
            }
        }
    }

    fun deleteAlloy(alloy: MetalAlloy) {
        viewModelScope.launch {
            try {
                repository.deleteAlloy(alloy)
            } catch (e: Exception) {
                _errorMessage.value = "Error while delete alloy: ${e.message}"
            }
        }
    }

    fun updateAlloy(alloy: MetalAlloy) {
        viewModelScope.launch {
            try {
                repository.updateAlloy(alloy)
            } catch (e: Exception) {
                _errorMessage.value = "Error while update alloy: ${e.message}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Factory for DI
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