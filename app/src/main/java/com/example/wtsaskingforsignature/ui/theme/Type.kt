package com.example.wtsaskingforsignature.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import com.example.wtsaskingforsignature.R

val AppFontFamily = try {
	FontFamily(Font(R.font.wts_kai))
} catch (e: Throwable) {
	FontFamily.Serif
}

// 字重與排版：標題 SemiBold、標題 Medium、內文 Regular；移除多餘 font padding
private val headlineStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.SemiBold,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

private val titleStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

private val bodyStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val WtsTypography: Typography = Typography().let { base ->
	Typography(
		displayLarge = base.displayLarge.merge(headlineStyle),
		displayMedium = base.displayMedium.merge(headlineStyle),
		displaySmall = base.displaySmall.merge(headlineStyle),
		headlineLarge = base.headlineLarge.merge(headlineStyle),
		headlineMedium = base.headlineMedium.merge(headlineStyle),
		headlineSmall = base.headlineSmall.merge(headlineStyle),
		titleLarge = base.titleLarge.merge(titleStyle),
		titleMedium = base.titleMedium.merge(titleStyle),
		titleSmall = base.titleSmall.merge(titleStyle),
		bodyLarge = base.bodyLarge.merge(bodyStyle),
		bodyMedium = base.bodyMedium.merge(bodyStyle),
		bodySmall = base.bodySmall.merge(bodyStyle),
		labelLarge = base.labelLarge.merge(bodyStyle),
		labelMedium = base.labelMedium.merge(bodyStyle),
		labelSmall = base.labelSmall.merge(bodyStyle)
	)
}
