package com.example.jewelryworkshop

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.jewelryworkshop.app.data.local.database.JewelryDatabase
import com.jewelryworkshop.app.data.repository.JewelryRepositoryImpl
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import com.jewelryworkshop.ui.MainViewModel

class JewelryWorkshopApp : Application(), ViewModelStoreOwner {
    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    lateinit var repository: JewelryRepository
        private set

    lateinit var mainViewModel: MainViewModel
        private set

    override fun onCreate() {
        super.onCreate()
        Log.d("JewelryWorkshopApp", "Application created")
        val database = JewelryDatabase.getInstance(this)
        val transactionDao = database.transactionDao()
        repository = JewelryRepositoryImpl(transactionDao)
        val factory = MainViewModel.Factory(repository)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}

val Application.app: JewelryWorkshopApp
    get() = this as JewelryWorkshopApp