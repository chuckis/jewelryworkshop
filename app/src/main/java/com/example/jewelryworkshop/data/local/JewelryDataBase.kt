package com.jewelryworkshop.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jewelryworkshop.data.local.MetalAlloyDao
import com.jewelryworkshop.app.data.local.entity.MetalAlloyEntity
import com.jewelryworkshop.app.data.local.dao.TransactionDao
import com.jewelryworkshop.app.data.local.entity.Converters
import com.jewelryworkshop.app.data.local.entity.TransactionEntity

/**
 * База данных Room для приложения ювелирной мастерской
 */
@TypeConverters(Converters::class)
@Database(
    entities = [TransactionEntity::class, MetalAlloyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JewelryDatabase : RoomDatabase() {

    /**
     * Получить DAO для работы с транзакциями
     */
    abstract fun transactionDao(): TransactionDao
    abstract fun metalAlloyDao(): MetalAlloyDao

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
                    //                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}