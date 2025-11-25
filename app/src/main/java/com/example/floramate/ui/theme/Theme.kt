package com.example.floramate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = White,
    primaryContainer = LightSageGreen,
    onPrimaryContainer = DeepSage,

    secondary = CoralPink,
    onSecondary = White,
    secondaryContainer = LightCoral,
    onSecondaryContainer = DarkCoral,

    tertiary = DeepSage,
    onTertiary = White,
    tertiaryContainer = LightSageGreen,
    onTertiaryContainer = DeepSage,

    background = OffWhite,
    onBackground = DarkGrey,

    surface = White,
    onSurface = DarkGrey,
    surfaceVariant = LightSageGreen,
    onSurfaceVariant = DeepSage,

    surfaceTint = SageGreen,
    inverseSurface = DarkGrey,
    inverseOnSurface = OffWhite,

    error = ErrorRed,
    onError = White,
    errorContainer = LightCoral,
    onErrorContainer = DarkCoral,

    outline = SageGreen,
    outlineVariant = LightSageGreen,
    scrim = DarkGrey
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = DarkGrey,
    primaryContainer = DeepSage,
    onPrimaryContainer = LightSageGreen,

    secondary = CoralPink,
    onSecondary = DarkGrey,
    secondaryContainer = DarkCoral,
    onSecondaryContainer = LightCoral,

    tertiary = LightSageGreen,
    onTertiary = DarkGrey,
    tertiaryContainer = DeepSage,
    onTertiaryContainer = LightSageGreen,

    background = DarkGrey,
    onBackground = OffWhite,

    surface = DarkGrey,
    onSurface = OffWhite,
    surfaceVariant = DeepSage,
    onSurfaceVariant = LightSageGreen,

    surfaceTint = SageGreen,
    inverseSurface = OffWhite,
    inverseOnSurface = DarkGrey,

    error = ErrorRed,
    onError = White,
    errorContainer = DarkCoral,
    onErrorContainer = LightCoral,

    outline = SageGreen,
    outlineVariant = DeepSage,
    scrim = DarkGrey
)

@Composable
fun FloraMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}