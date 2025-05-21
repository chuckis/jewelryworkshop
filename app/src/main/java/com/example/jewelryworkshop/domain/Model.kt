package com.jewelryworkshop.app.domain.model

import java.time.LocalDateTime

/**
 * Перечисление типов операций с металлами/изделиями
 */
enum class TransactionType {
    RECEIVED, // Получено
    ISSUED    // Выдано
}

/**
 * Доменная модель для операции с металлами/изделиями
 */
data class Transaction(
    val id: Long = 0,
    val dateTime: LocalDateTime, // Дата и время операции
    val weight: Double, // Вес металла/изделий в граммах
    val type: TransactionType, // Тип операции (получено/выдано)
    val description: String, // Описание операции
    val itemsCount: Int?,// Количество изделий
    val alloy: MetalAlloy,
) {
    /**
     * Получить строковое представление транзакции в формате:
     * "Дата(время); Вес полученых/выданых металла/изделий; Описание: кол-во изделий"
     */
    fun toFormattedString(): String {
        val typeStr = if (type == TransactionType.RECEIVED) "получено" else "выдано"
        return "$dateTime; $weight г. $typeStr; $description: $itemsCount шт."
    }
}

/**
 * Доменная модель для отчета по балансу металлов/изделий
 */
data class MetalBalance(
    val totalWeight: Double, // Общий вес металла/изделий в граммах
    val totalItems: Int?, // Общее количество изделий
    val transactions: List<Transaction> = emptyList() // Связанные транзакции
)

data class MetalAlloy(
    val id: Long,
    val name: String,
)