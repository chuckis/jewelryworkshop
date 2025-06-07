package com.example.jewelryworkshop.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jewelryworkshop.app.domain.model.MetalAlloy

@Dao
interface MetalAlloyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metalAlloy: MetalAlloy)

    @Update
    suspend fun update(metalAlloy: MetalAlloy)

    @Delete
    suspend fun delete(metalAlloy: MetalAlloy)

    @Query("SELECT * FROM metal_alloys")
    suspend fun getAll(): List<MetalAlloy>

    @Query("SELECT * FROM metal_alloys WHERE id = :id")
    suspend fun getById(id: Long): MetalAlloy?
}