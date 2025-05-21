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

    private val database: JewelryDatabase by lazy {
        JewelryDatabase.getInstance(this)
    }

    val repository: JewelryRepository by lazy {
        JewelryRepositoryImpl(database.transactionDao())
    }

    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(repository))[MainViewModel::class.java]
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("JewelryWorkshopApp", "Application created")
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}

val Application.app: JewelryWorkshopApp
    get() = this as JewelryWorkshopApp