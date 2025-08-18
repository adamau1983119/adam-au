package com.example.wtsaskingforsignature.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
		content = content
	)
}
