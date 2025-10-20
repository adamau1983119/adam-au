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
    fun generateTerm(starType: StarType, palaceType: PalaceType): String {
        return when (starType) {
            StarType.HUA_LU -> when (palaceType) {
                PalaceType.OFFICIAL -> "事業宮見吉星化祿"
                PalaceType.MING -> "命宮見吉星化祿"
                else -> "見吉星化祿"
            }
            StarType.HUA_KE -> when (palaceType) {
                PalaceType.OFFICIAL -> "流年官祿宮有貴人星輔助"
                PalaceType.MING -> "命宮有貴人星輔助"
                else -> "有貴人星輔助"
            }
            StarType.HUA_JI -> when (palaceType) {
                PalaceType.OFFICIAL -> "事業宮見化忌"
                PalaceType.MING -> "命宮見化忌"
                else -> "見化忌"
            }
            else -> ""
        }
    }
}
