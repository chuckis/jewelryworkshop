package com.jewelryworkshop.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import com.jewelryworkshop.app.data.local.entity.MetalAlloyEntity
import com.jewelryworkshop.app.domain.model.Transaction
import com.jewelryworkshop.app.domain.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Сущность Room для хранения транзакций в базе данных
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = MetalAlloyEntity::class,
            parentColumns = ["id"],
            childColumns = ["alloyId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["alloyId"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTime: LocalDateTime,
    val weight: Double,
    val type: TransactionType,
    val description: String,
    val itemsCount: Int?,
    val alloyId: Long,
) {
    fun toDomain(alloy: MetalAlloyEntity): Transaction = Transaction(
        id = id,
        dateTime = dateTime,
        weight = weight,
        type = type,
        description = description,
        itemsCount = itemsCount,
        alloy = alloy.toDomain(),
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
            itemsCount = transaction.itemsCount,
            alloyId = transaction.alloy.id,
        )
    }
}

/**
 * Конвертеры типов для Room
 */
class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }

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

data class TransactionWithAlloy(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "alloyId",
        entityColumn = "id"
    )
    val alloy: MetalAlloyEntity
)