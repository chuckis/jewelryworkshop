package com.example.jewelryworkshop


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.jewelryworkshop.ui.AppNavigation
import com.example.jewelryworkshop.ui.theme.JewelryWorkshopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel = application.app.mainViewModel
        val reportManagementViewModel = application.app.reportManagementViewModel

        setContent {
            JewelryWorkshopTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(mainViewModel, reportManagementViewModel)
                }
            }
        }
    }
}