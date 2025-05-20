package com.example.jewelryworkshop

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.jewelryworkshop.ui.AppNavigation
import com.example.jewelryworkshop.ui.theme.JewelryWorkshopTheme

/**
 * Основная активность приложения
 */
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получение экземпляра ViewModel из приложения
        val mainViewModel = application.app.mainViewModel

        setContent {
            JewelryWorkshopTheme {
                // Создание основной поверхности приложения
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Инициализация навигации приложения
                    AppNavigation(mainViewModel)
                }
            }
        }
    }
}