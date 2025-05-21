import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jewelryworkshop.data.local.MetalAlloyEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MetalAlloyDao {
    @Query("SELECT * FROM metal_alloys")
    fun getAllMetalAlloys(): Flow<List<MetalAlloyEntity>>

    @Query("SELECT * FROM metal_alloys WHERE id = :id")
    suspend fun getMetalAlloyById(id: Long): MetalAlloyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetalAlloy(metalAlloy: MetalAlloyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetalAlloys(metalAlloys: List<MetalAlloyEntity>): List<Long>

    @Update
    suspend fun updateMetalAlloy(metalAlloy: MetalAlloyEntity)

    @Delete
    suspend fun deleteMetalAlloy(metalAlloy: MetalAlloyEntity)

    @Query("DELETE FROM metal_alloys WHERE id = :id")
    suspend fun deleteMetalAlloyById(id: Long)

    @Query("DELETE FROM metal_alloys")
    suspend fun deleteAllMetalAlloys()
}