package com.jewelryworkshop.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jewelryworkshop.app.data.local.dao.TransactionDao
import com.jewelryworkshop.app.data.local.entity.Converters
import com.jewelryworkshop.app.data.local.entity.TransactionEntity

/**
 * База данных Room для приложения ювелирной мастерской
 */
@Database(
    entities = [TransactionEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JewelryDatabase : RoomDatabase() {

    /**
     * Получить DAO для работы с транзакциями
     */
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: JewelryDatabase? = null

        fun getInstance(context: Context): JewelryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    JewelryDatabase::class.java,
                    "jewelry_database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}