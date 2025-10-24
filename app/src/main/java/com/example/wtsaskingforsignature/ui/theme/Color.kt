package com.example.wtsaskingforsignature.ui.theme

import androidx.compose.ui.graphics.Color

// Version 21 優化：Morandi（莫蘭迪）偏藕粉色系（提升對比度和視覺層次）
val MdPrimary = Color(0xFFD4A5AC)      // 主色：更深的藕粉玫瑰，提升對比度
val MdOnPrimary = Color(0xFFFFFFFF)     // 主色上的文字：保持純白
val MdSecondary = Color(0xFFE8C8CE)     // 次色：適中的藕粉，平衡視覺層次
val MdBackground = Color(0xFFF8F2F3)    // 背景：更亮的暖淡粉，提升可讀性
val MdSurface = Color(0xFFF3EDEE)       // 表面/卡片：優化對比度，突出內容
val MdOnSurface = Color(0xFF2A1F20)     // 表面文字：加深顏色，提升可讀性
val MdOutline = Color(0xFFD1C2C6)       // 邊框/分隔：優化對比度

// 漸層背景（Version 21 優化：更柔和的漸層過渡）
val MdGradientTop = Color(0xFFF9F0F1)
val MdGradientBottom = Color(0xFFF2E6E8)

// Version 21 優化：深色模式對應（調整文字顏色，避免白色文字）
val MdPrimaryDark = Color(0xFFD4A5AC)      // 主色：與淺色模式保持一致
val MdOnPrimaryDark = Color(0xFF2A1F20)     // 主色上的文字：改為深色，與淺色模式一致
val MdSecondaryDark = Color(0xFFE8C8CE)     // 次色：與淺色模式保持一致
val MdBackgroundDark = Color(0xFF121212)    // 背景：使用Material Design深色背景
val MdSurfaceDark = Color(0xFF1E1E1E)       // 表面色：優化對比度，提升層次感
val MdOnSurfaceDark = Color(0xFF2A1F20)     // 表面文字：改為深色，與淺色模式一致
val MdOutlineDark = Color(0xFF4A4A4A)       // 邊框：優化對比度，提升可見性
