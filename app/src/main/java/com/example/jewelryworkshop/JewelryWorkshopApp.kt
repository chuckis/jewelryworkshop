package com.example.jewelryworkshop

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.jewelryworkshop.app.data.local.database.JewelryDatabase
import com.jewelryworkshop.app.data.repository.JewelryRepositoryImpl
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import com.jewelryworkshop.ui.MainViewModel

/**
 * Класс приложения для ювелирной мастерской
 */
class JewelryWorkshopApp : Application(), ViewModelStoreOwner {

    // ViewModelStore для хранения ViewModel на уровне приложения
    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    // Репозиторий для работы с данными
    lateinit var repository: JewelryRepository
        private set

    // Основная ViewModel приложения
    lateinit var mainViewModel: MainViewModel
        private set

    override fun onCreate() {
        super.onCreate()

        // Инициализация базы данных
        val database = JewelryDatabase.getInstance(this)
        val transactionDao = database.transactionDao()

        // Создание репозитория
        repository = JewelryRepositoryImpl(transactionDao)

        // Создание ViewModel
        val factory = MainViewModel.Factory(repository)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}

/**
 * Расширение для получения экземпляра приложения
 */
val Application.app: JewelryWorkshopApp
    get() = this as JewelryWorkshopApp