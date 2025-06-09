package com.example.jewelryworkshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jewelryworkshop.domain.MetalAlloy

@Entity(tableName = "metal_alloys")
data class MetalAlloyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
) {
    fun toDomain(): MetalAlloy = MetalAlloy(
        id = id,
        name = name,
    )

    companion object {
        fun fromDomain(metalAlloy: MetalAlloy): MetalAlloyEntity = MetalAlloyEntity(
            id = metalAlloy.id,
            name = metalAlloy.name,
        )
    }
}