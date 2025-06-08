package com.example.jewelryworkshop.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jewelryworkshop.app.domain.model.MetalAlloy
import kotlinx.coroutines.flow.Flow

@Dao
interface MetalAlloyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metalAlloy: MetalAlloyEntity): Long

    @Update
    suspend fun update(metalAlloy: MetalAlloyEntity)

    @Delete
    suspend fun delete(metalAlloy: Long)

    @Query("SELECT * FROM metal_alloys")
    fun getAllAlloys(): List<MetalAlloy>

    @Query("SELECT * FROM metal_alloys WHERE id = :id")
    suspend fun getById(id: Long): MetalAlloy?
}