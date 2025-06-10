package com.example.jewelryworkshop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jewelryworkshop.data.local.entity.MetalAlloyEntity
import com.example.jewelryworkshop.data.local.entity.Converters
import com.example.jewelryworkshop.data.local.entity.TransactionEntity

/**
 * База данных Room для приложения ювелирной мастерской
 */
@Database(
    entities = [TransactionEntity::class, MetalAlloyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JewelryDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun metalAlloyDao(): MetalAlloyDao

    companion object {
        @Volatile
        private var INSTANCE: JewelryDatabase? = null

        fun getInstance(context: Context): JewelryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JewelryDatabase::class.java,
                    "jewelry_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}