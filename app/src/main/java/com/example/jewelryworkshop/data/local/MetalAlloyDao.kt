package com.example.jewelryworkshop.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jewelryworkshop.data.local.entity.MetalAlloyEntity
import com.example.jewelryworkshop.domain.MetalAlloy
import kotlinx.coroutines.flow.Flow

@Dao
interface MetalAlloyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metalAlloy: MetalAlloyEntity): Long

    @Update
    suspend fun update(metalAlloy: MetalAlloyEntity)

    @Delete
    suspend fun delete(metalAlloy: MetalAlloyEntity)

    @Query("SELECT * FROM metal_alloys")
    fun getAllAlloys(): Flow<List<MetalAlloyEntity>>

    @Query("SELECT * FROM metal_alloys WHERE id = :id")
    suspend fun getByIdInternal(id: Long): MetalAlloyEntity

    suspend fun getById(metalAlloy: MetalAlloy): MetalAlloyEntity {
        return getByIdInternal(metalAlloy.id)
    }

}