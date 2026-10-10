package com.client.androidnga.ui.compose3.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.justwen.androidnga.ui.compose.theme.PrimaryBlack
import com.justwen.androidnga.ui.compose.theme.PrimaryBrown
import com.justwen.androidnga.ui.compose.theme.PrimaryGreen
import com.justwen.androidnga.ui.compose.theme.PrimaryNight


private val defaultLDarkColorScheme =
    darkColorScheme(primary = PrimaryNight, background = Color(0XFF080C10))
private val defaultLightColorScheme = lightColorScheme(background = Color(0xFFFFF8E7))

private val greenLightColorScheme = defaultLightColorScheme.copy(primary = PrimaryGreen)

private val blackLightColorScheme = defaultLightColorScheme.copy(primary = PrimaryBlack)

private val brownLightColorScheme = defaultLightColorScheme.copy(primary = PrimaryBrown)

private val lightColorSchemes =
    arrayOf(brownLightColorScheme, greenLightColorScheme, blackLightColorScheme)

@Composable
fun AppTheme(
    dark: Boolean = isSystemInDarkTheme(),
    themeIndex: Int = 1,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dark) defaultLDarkColorScheme else lightColorSchemes[themeIndex]
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}