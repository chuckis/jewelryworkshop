package com.example.jewelryworkshop

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.jewelryworkshop.domain.RepositoryFactory
import com.jewelryworkshop.app.domain.repository.JewelryRepository
import com.jewelryworkshop.app.domain.repository.RepositoryType
import com.jewelryworkshop.ui.MainViewModel

class JewelryWorkshopApp : Application(), ViewModelStoreOwner {
    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    companion object {
        // Временные константы вместо BuildConfig
        private const val DEFAULT_REPOSITORY = "ROOM_DATABASE"
        private val ENABLE_REPOSITORY_SELECTOR = isDebuggable()

        private fun isDebuggable(): Boolean {
            return try {
                // Определяем debug режим через ApplicationInfo
                val context = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as? Application

                context?.let {
                    (it.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun getDefaultRepositoryType(): RepositoryType {
        return try {
            RepositoryType.valueOf(DEFAULT_REPOSITORY)
        } catch (e: IllegalArgumentException) {
            RepositoryType.ROOM_DATABASE
        }
    }

    private fun getCurrentRepositoryType(): RepositoryType {
        return if (ENABLE_REPOSITORY_SELECTOR) {
            getCurrentRepositoryTypeFromPrefs(this)
        } else {
            getDefaultRepositoryType()
        }
    }

    private fun getCurrentRepositoryTypeFromPrefs(context: Context): RepositoryType {
        val prefs = context.getSharedPreferences("developer_settings", Context.MODE_PRIVATE)
        val typeName = prefs.getString("repository_type", getDefaultRepositoryType().name)
        return try {
            RepositoryType.valueOf(typeName ?: getDefaultRepositoryType().name)
        } catch (e: IllegalArgumentException) {
            getDefaultRepositoryType()
        }
    }

    val repository: JewelryRepository by lazy {
        val type = getCurrentRepositoryType()
        Log.d("JewelryWorkshopApp", "Creating repository of type: $type")
        RepositoryFactory.createJewelryRepository(this, type)
    }

    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(repository))[MainViewModel::class.java]
    }

    override fun onCreate() {
        super.onCreate()
        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        Log.d("JewelryWorkshopApp", "Application created with repository: ${getCurrentRepositoryType()}")
        Log.d("JewelryWorkshopApp", "Debug mode: $isDebug")
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore
}

val Application.app: JewelryWorkshopApp
    get() = this as JewelryWorkshopApp