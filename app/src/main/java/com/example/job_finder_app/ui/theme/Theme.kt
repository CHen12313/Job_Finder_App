package com.example.job_finder_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

// Similar to React context, LocalPalette is the context, LightPalette is the default
val LocalPalette = staticCompositionLocalOf { LightPalette }

private val DarkColorScheme = darkColorScheme(
    primary = DarkPalette.primary,
    onPrimary = DarkPalette.primaryInk,
    background = DarkPalette.bg,
    onBackground = DarkPalette.ink,
    primaryContainer = DarkPalette.primarySoft,
    onPrimaryContainer = DarkPalette.primaryDeep,
    surface = DarkPalette.surf,
    onSurface = DarkPalette.ink,
    surfaceVariant = DarkPalette.surf2,
    onSurfaceVariant = DarkPalette.muted,
    outline = DarkPalette.lineStrong,
    outlineVariant = DarkPalette.line
)

private val LightColorScheme = lightColorScheme(
    primary = LightPalette.primary,
    onPrimary = LightPalette.primaryInk,
    background = LightPalette.bg,
    onBackground = LightPalette.ink,
    primaryContainer = LightPalette.primarySoft,
    onPrimaryContainer = LightPalette.primaryDeep,
    surface = LightPalette.surf,
    onSurface = LightPalette.ink,
    surfaceVariant = LightPalette.surf2,
    onSurfaceVariant = LightPalette.muted,
    outline = LightPalette.lineStrong,
    outlineVariant = LightPalette.line
)

@Composable
fun Job_Finder_AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val palette = when {
        darkTheme -> DarkPalette
        else -> LightPalette
    }

    CompositionLocalProvider(LocalPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Kotlin version of React's useContext. Which calls the value inside the context object
val MaterialTheme.palette: Palette
    @Composable
    get() = LocalPalette.current