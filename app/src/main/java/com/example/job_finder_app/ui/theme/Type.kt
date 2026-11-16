package com.example.job_finder_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.job_finder_app.R

val PlusJakarta = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    )

// Default styles
private val base = Typography()

// Old version of font style
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//)

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = PlusJakarta),
    displayMedium = base.displayMedium.copy(fontFamily = PlusJakarta),
    displaySmall = base.displaySmall.copy(fontFamily = PlusJakarta),
    headlineLarge = base.headlineLarge.copy(fontFamily = PlusJakarta),
    headlineMedium = base.headlineMedium.copy(fontFamily = PlusJakarta),
    headlineSmall = base.headlineSmall.copy(fontFamily = PlusJakarta),
    titleLarge = base.titleLarge.copy(fontFamily = PlusJakarta),
    titleMedium = base.titleMedium.copy(fontFamily = PlusJakarta),
    titleSmall = base.titleSmall.copy(fontFamily = PlusJakarta),
    bodyLarge = base.bodyLarge.copy(fontFamily = PlusJakarta),
    bodyMedium = base.bodyMedium.copy(fontFamily = PlusJakarta),
    bodySmall = base.bodySmall.copy(fontFamily = PlusJakarta),
    labelLarge = base.labelLarge.copy(fontFamily = PlusJakarta),
    labelMedium = base.labelMedium.copy(fontFamily = PlusJakarta),
    labelSmall = base.labelSmall.copy(fontFamily = PlusJakarta),
)