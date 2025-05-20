package com.jewelryworkshop.app.data.local.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Сущность Room для хранения транзакций в базе данных
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateTime: LocalDateTime,
    val weight: Double,
    val type: TransactionType,
    val description: String,
    val itemsCount: Int
) {
    /**
     * Преобразовать Entity в доменную модель
     */
    fun toDomain(): Transaction = Transaction(
        id = id,
        dateTime = dateTime,
        weight = weight,
        type = type,
        description = description,
        itemsCount = itemsCount
    )

    companion object {
        /**
         * Преобразовать доменную модель в Entity
         */
        fun fromDomain(transaction: Transaction): TransactionEntity = TransactionEntity(
            id = transaction.id,
            dateTime = transaction.dateTime,
            weight = transaction.weight,
            type = transaction.type,
            description = transaction.description,
            itemsCount = transaction.itemsCount
        )
    }
}

/**
 * Конвертеры типов для Room
 */
class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}

