package com.example.wtsaskingforsignature.data.EnhancedModels

import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.data.EnhancedContext.Gender

// 新增資料結構 - 月度分析
data class MonthlyAnalysis(
    val october: String = "",
    val november: String = "",
    val december: String = ""
)

// 專業術語映射
data class ProfessionalTerminology(
    val huaLu: String = "吉星化祿",
    val huaKe: String = "貴人星輔助", 
    val huaJi: String = "化忌",
    val careerTrend: String = "工作運勢漸入佳境"
)

// 籤文寓意映射
data class FortuneMeaning(
    val id: Int,
    val title: String,
    val summary: String,
    val content: String,
    val meaning: String = "",
    val symbol: String = ""
)

// 解析後的時間範圍
data class ParsedTimeRange(
    val year: Int? = null,
    val startMonth: Int? = null,
    val endMonth: Int? = null
)

// 紫微相關資料（最小可行版，與 Integrator 對齊）
data class ZiweiAnalysis(
    val mingGong: StarAnalysis? = null,
    val shenGong: StarAnalysis? = null,
    val careerPalace: PalaceAnalysis? = null,
    val lovePalace: PalaceAnalysis? = null,
    val wealthPalace: PalaceAnalysis? = null,
    val healthPalace: PalaceAnalysis? = null,
    val liuNian: LiuNianAnalysis? = null
)

data class StarAnalysis(
    val mainStar: String = "",
    val secondaryStars: List<String> = emptyList(),
    val siHua: String = ""
)

data class PalaceAnalysis(
    val mainStar: String = "",
    val secondaryStars: List<String> = emptyList(),
    val siHua: String = "",
    val strength: String = ""
)

data class LiuNianAnalysis(
    val year: Int = 2025,
    val careerTrend: String = "",
    val keyMonths: List<String> = emptyList(),
    val opportunities: String = ""
)

// 基本用戶資料
data class UserProfile(
    val name: String = "用戶",
    val age: Int = 30,
    val gender: Gender = Gender.UNKNOWN,
    val birthDate: String = "1990-01-01",
    val birthTime: String = "12:00",
    val birthPlace: String = "香港",
    val hasValidData: Boolean = false  // 是否有真實的個人資料
)

// 擴充方法（供語意模組使用）
fun PalaceAnalysis.hasHuaLu(): Boolean = siHua.contains("祿")
fun PalaceAnalysis.hasHuaKe(): Boolean = siHua.contains("科")
fun PalaceAnalysis.hasHuaJi(): Boolean = siHua.contains("忌")
