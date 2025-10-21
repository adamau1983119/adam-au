package com.example.wtsaskingforsignature.utils

import com.example.wtsaskingforsignature.data.EnhancedModels.ParsedTimeRange

// 時間範圍解析器
class TimeRangeParser {
    fun parseTimeRange(timeRange: String): ParsedTimeRange {
        return when {
            timeRange.contains("10-12月") || timeRange.contains("10至12月") -> 
                ParsedTimeRange(startMonth = 10, endMonth = 12)
            timeRange.contains("2025") -> 
                ParsedTimeRange(year = 2025)
            timeRange.contains("年底") -> 
                ParsedTimeRange(startMonth = 10, endMonth = 12)
            else -> ParsedTimeRange()
        }
    }
}

// 專業術語生成器
class ProfessionalTermGenerator {
    fun generateTerm(starType: String, palaceType: String): String {
        return when {
            starType.contains("祿") && palaceType.contains("官祿") -> "事業宮見吉星化祿"
            starType.contains("祿") && palaceType.contains("命宮") -> "命宮見吉星化祿"
            starType.contains("科") && palaceType.contains("官祿") -> "流年官祿宮有貴人星輔助"
            starType.contains("科") && palaceType.contains("命宮") -> "命宮有貴人星輔助"
            starType.contains("忌") && palaceType.contains("官祿") -> "事業宮見化忌"
            starType.contains("忌") && palaceType.contains("命宮") -> "命宮見化忌"
            else -> ""
        }
    }
}
