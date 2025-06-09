package com.example.jewelryworkshop.domain

import android.content.Context
import com.example.jewelryworkshop.data.local.CombinedRepository
import com.example.jewelryworkshop.data.local.CombinedRepositoryInMemory
import com.example.jewelryworkshop.data.local.CombinedRepositoryMock
import com.example.jewelryworkshop.data.local.JewelryDatabase
import com.example.jewelryworkshop.data.local.CombinedRepositoryRoom

object RepositoryFactory {

    // Кеширование созданных репозиториев
    private var roomRepository: CombinedRepositoryRoom? = null
    private var inMemoryRepository: CombinedRepositoryInMemory? = null
    private var mockRepository: CombinedRepositoryMock? = null

    /**
     * Создает репозиторий указанного типа
     */
    fun createJewelryRepository(
        context: Context,
        type: RepositoryType = RepositoryType.ROOM_DATABASE
    ): CombinedRepository {
        return when (type) {
            RepositoryType.ROOM_DATABASE -> {
                if (roomRepository == null) {
                    roomRepository = createRoomRepository(context)
                }
                roomRepository!!
            }
            RepositoryType.IN_MEMORY_TEST -> {
                if (inMemoryRepository == null) {
                    inMemoryRepository = CombinedRepositoryInMemory()
                }
                inMemoryRepository!!
            }
            RepositoryType.MOCK_DATA -> {
                if (mockRepository == null) {
                    mockRepository = CombinedRepositoryMock()
                }
                mockRepository!!
            }
        }
    }
    /**
     * Создает Room репозиторий
     */
    private fun createRoomRepository(context: Context): CombinedRepositoryRoom {
        val database = JewelryDatabase.getInstance(context)
        return CombinedRepositoryRoom(
            transactionDao = database.transactionDao(),
            metalAlloyDao = database.metalAlloyDao()
        )
    }

    /**
     * Очистка кеша (полезно для тестов)
     */
    @Synchronized
    fun clearCache() {
        roomRepository = null
        inMemoryRepository = null
        mockRepository = null
    }
}
