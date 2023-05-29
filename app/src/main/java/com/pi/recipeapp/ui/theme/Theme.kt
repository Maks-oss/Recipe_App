package com.pi.recipeapp.ui.theme

import android.R.id.primary
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController


private val DarkColorPalette = darkColors(
    primary = Color(0xFF5DA9C9),
    primaryVariant = Color(0xFF012F40),
    onPrimary = Color.White,
    secondary = Color(0xFF1D3557),
    onSecondary = Color.White,
    surface = Color(0xFF0B304A),
    onSurface = Color.White,
//    background = Color(0xFF03045E),
    onBackground = Color.White,
)

val LightColorPalette = lightColors(
    primary = Color(0xFF035E7B),
    primaryVariant = Color(0xFF024E63),
    onPrimary = Color.White,
    secondary = Color(0xFF09A5E0),
    onSecondary = Color.White,
    surface = Color(0xFFE8F6F8),
    onSurface = Color.Black,
//    background = Color(0xFFF0FFFF)
    background = Color(0xFFFFFFFF)
)

@Composable
fun RecipeAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    systemUiController.setStatusBarColor(colors.primaryVariant)
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}