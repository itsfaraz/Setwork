package com.designlife.setwork.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.designlife.setwork.settings.presentation.enums.AppFontSize
import com.designlife.setwork.settings.presentation.enums.AppTheme
import com.designlife.setwork.settings.presentation.viewmodel.SettingViewModel


private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun SetworkComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

fun updateSystemUIMode(value: AppTheme) {
    when(value){
        AppTheme.LIGHT -> {
            SettingViewModel.updateDarkModeSetting(false)
        }
        AppTheme.DARK -> {
            SettingViewModel.updateDarkModeSetting(true)
        }
    }
}