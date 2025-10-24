package com.example.wtsaskingforsignature.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.wtsaskingforsignature.R

val AppFontFamily = try {
	FontFamily(Font(R.font.wts_kai))
} catch (e: Throwable) {
	FontFamily.Serif
}

// Version 21 優化：字重與排版（提升可讀性和視覺層次）
private val headlineStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Bold,        // 提升標題字重，增強視覺層次
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeight = 1.2.sp                  // 優化行高，提升可讀性
)

private val titleStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.SemiBold,    // 提升標題字重
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeight = 1.3.sp                  // 優化行高
)

private val bodyStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeight = 1.5.sp                  // 優化行高，提升閱讀體驗
)

// Version 21 新增：特殊用途字體樣式
private val buttonStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Medium,      // 按鈕使用中等字重
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeight = 1.0.sp                  // 按鈕使用緊湊行高
)

private val captionStyle = TextStyle(
    fontFamily = AppFontFamily,
    fontWeight = FontWeight.Normal,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeight = 1.4.sp                  // 說明文字適中行高
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
