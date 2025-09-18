package com.example.wtsaskingforsignature.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
	primary = MdPrimary,
	onPrimary = MdOnPrimary,
	secondary = MdSecondary,
	background = MdBackground,
	surface = MdSurface,
	onSurface = MdOnSurface,
	outline = MdOutline
)

private val DarkColors = darkColorScheme(
	primary = MdPrimaryDark,
	onPrimary = MdOnPrimaryDark,
	secondary = MdSecondaryDark,
	background = MdBackgroundDark,
	surface = MdSurfaceDark,
	onSurface = MdOnSurfaceDark,
	outline = MdOutlineDark
)

@Composable
fun WtsTheme(
	useDarkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	val colors = if (useDarkTheme) DarkColors else LightColors
	MaterialTheme(
		colorScheme = colors,
		typography = WtsTypography,
		shapes = WtsShapes,
		content = content
	)
}

// App shapes：接近設計稿的 12–24dp 圓角
val WtsShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
