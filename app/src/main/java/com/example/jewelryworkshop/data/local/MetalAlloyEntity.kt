package com.example.jewelryworkshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jewelryworkshop.app.domain.model.MetalAlloy

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