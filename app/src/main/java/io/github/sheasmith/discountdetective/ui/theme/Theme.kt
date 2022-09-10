package io.github.sheasmith.discountdetective.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * Sets our own colour scheme.
 *
 * @param darkTheme Whether the dark theme should be used or not.
 * @param doNotOverride Whether the status bar colours should not be overriden.
 * @param content The content of the page.
 */
@Composable
fun DiscountDetectiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    doNotOverride: Boolean = false,
    content: @Composable () -> Unit
) {
    if (!doNotOverride) {
        val systemUiController = rememberSystemUiController()

        DisposableEffect(systemUiController, darkTheme) {
            // Update all of the system bar colors to be transparent, and use
            // dark icons if we're in light theme
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !darkTheme
            )

            // setStatusBarColor() and setNavigationBarColor() also exist

            onDispose {}
        }
    }

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}