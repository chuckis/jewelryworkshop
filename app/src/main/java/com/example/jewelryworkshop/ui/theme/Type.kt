package com.example.jewelryworkshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Load custom fonts from your res/font directory
// Make sure to add these font files to your project in the res/font directory
// Alternatively, if you don't want to use custom fonts, you can use system fonts:
// val DefaultFontFamily = FontFamily.Default

// Define the Typography for your application
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)