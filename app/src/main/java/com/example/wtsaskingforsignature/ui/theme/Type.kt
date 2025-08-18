package com.example.wtsaskingforsignature.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.wtsaskingforsignature.R

val AppFontFamily = try {
	FontFamily(Font(R.font.wts_kai))
} catch (e: Throwable) {
	FontFamily.Serif
}

val WtsTypography: Typography = Typography().let { base ->
	Typography(
		displayLarge = base.displayLarge.copy(fontFamily = AppFontFamily),
		displayMedium = base.displayMedium.copy(fontFamily = AppFontFamily),
		displaySmall = base.displaySmall.copy(fontFamily = AppFontFamily),
		headlineLarge = base.headlineLarge.copy(fontFamily = AppFontFamily),
		headlineMedium = base.headlineMedium.copy(fontFamily = AppFontFamily),
		headlineSmall = base.headlineSmall.copy(fontFamily = AppFontFamily),
		titleLarge = base.titleLarge.copy(fontFamily = AppFontFamily),
		titleMedium = base.titleMedium.copy(fontFamily = AppFontFamily),
		titleSmall = base.titleSmall.copy(fontFamily = AppFontFamily),
		bodyLarge = base.bodyLarge.copy(fontFamily = AppFontFamily),
		bodyMedium = base.bodyMedium.copy(fontFamily = AppFontFamily),
		bodySmall = base.bodySmall.copy(fontFamily = AppFontFamily),
		labelLarge = base.labelLarge.copy(fontFamily = AppFontFamily),
		labelMedium = base.labelMedium.copy(fontFamily = AppFontFamily),
		labelSmall = base.labelSmall.copy(fontFamily = AppFontFamily)
	)
}
