package com.example.jewelryworkshop.domain

import android.content.Context
import com.example.jewelryworkshop.data.local.JewelryRepositoryInMemory
import com.example.jewelryworkshop.data.local.JewelryRepositoryMock
import com.jewelryworkshop.app.data.local.database.JewelryDatabase
import com.jewelryworkshop.app.data.repository.JewelryRepositoryRoom
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import com.jewelryworkshop.app.domain.repository.RepositoryType

object RepositoryFactory {

    // Кеширование созданных репозиториев
    private var roomRepository: JewelryRepository? = null
    private var inMemoryRepository: JewelryRepository? = null
    private var mockRepository: JewelryRepository? = null

    /**
     * Создает репозиторий указанного типа
     */
    fun createJewelryRepository(
        context: Context,
        type: RepositoryType = RepositoryType.ROOM_DATABASE
    ): JewelryRepository {
        return when (type) {
            RepositoryType.ROOM_DATABASE -> {
                if (roomRepository == null) {
                    roomRepository = createRoomRepository(context)
                }
                roomRepository!!
            }
            RepositoryType.IN_MEMORY_TEST -> {
                if (inMemoryRepository == null) {
                    inMemoryRepository = JewelryRepositoryInMemory()
                }
                inMemoryRepository!!
            }
            RepositoryType.MOCK_DATA -> {
                if (mockRepository == null) {
                    mockRepository = JewelryRepositoryMock()
                }
                mockRepository!!
            }
        }
    }
    /**
     * Создает Room репозиторий
     */
    private fun createRoomRepository(context: Context): JewelryRepository {
        val database = JewelryDatabase.getInstance(context)
        return JewelryRepositoryRoom(
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
