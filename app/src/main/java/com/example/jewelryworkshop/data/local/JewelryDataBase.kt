package com.jewelryworkshop.app.data.local.database

import MetalAlloyDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jewelryworkshop.data.local.MetalAlloyEntity
import com.jewelryworkshop.app.data.local.dao.TransactionDao
import com.jewelryworkshop.app.data.local.entity.Converters
import com.jewelryworkshop.app.data.local.entity.TransactionEntity

/**
 * База данных Room для приложения ювелирной мастерской
 */
@Database(
    entities = [TransactionEntity::class, MetalAlloyEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JewelryDatabase : RoomDatabase() {

    /**
     * Получить DAO для работы с транзакциями
     */
    abstract fun transactionDao(): TransactionDao
    abstract fun metalAlloyDao(): MetalAlloyDao

    companion object {
        private const val DATABASE_NAME = "jewelry_workshop.db"

        @Volatile
        private var INSTANCE: JewelryDatabase? = null

        /**
         * Получить экземпляр базы данных
         */
        fun getInstance(context: Context): JewelryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JewelryDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}