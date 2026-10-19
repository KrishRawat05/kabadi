package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EcoGreenPrimaryDark,
    onPrimary = EcoGreenOnPrimaryDark,
    primaryContainer = EcoGreenPrimaryContainerDark,
    onPrimaryContainer = EcoGreenOnPrimaryContainerDark,
    secondary = EcoAmberSecondaryDark,
    onSecondary = EcoAmberOnSecondaryDark,
    secondaryContainer = EcoAmberSecondaryContainerDark,
    onSecondaryContainer = EcoAmberOnSecondaryContainerDark,
    tertiary = EcoTealTertiaryDark,
    onTertiary = EcoTealOnTertiaryDark,
    tertiaryContainer = EcoTealTertiaryContainerDark,
    onTertiaryContainer = EcoTealOnTertiaryContainerDark,
    background = EcoBackgroundDark,
    onBackground = EcoOnBackgroundDark,
    surface = EcoSurfaceDark,
    onSurface = EcoOnSurfaceDark,
    surfaceVariant = EcoSurfaceVariantDark,
    onSurfaceVariant = EcoOnSurfaceVariantDark,
    outline = EcoOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = EcoGreenPrimary,
    onPrimary = EcoGreenOnPrimary,
    primaryContainer = EcoGreenPrimaryContainerLight,
    onPrimaryContainer = EcoGreenOnPrimaryContainer,
    secondary = EcoAmberSecondary,
    onSecondary = EcoAmberOnSecondary,
    secondaryContainer = EcoAmberSecondaryContainer,
    onSecondaryContainer = EcoAmberOnSecondaryContainer,
    tertiary = EcoTealTertiary,
    onTertiary = EcoTealOnTertiary,
    tertiaryContainer = EcoTealTertiaryContainer,
    onTertiaryContainer = EcoTealOnTertiaryContainer,
    background = EcoBackgroundLight,
    onBackground = EcoOnBackgroundLight,
    surface = EcoSurfaceLight,
    onSurface = EcoOnSurfaceLight,
    surfaceVariant = EcoSurfaceVariantLight,
    onSurfaceVariant = EcoOnSurfaceVariantLight,
    outline = EcoOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to preserve distinctive ScrapCycle eco-branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
