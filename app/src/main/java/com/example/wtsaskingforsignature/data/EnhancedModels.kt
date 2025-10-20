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
    val fortuneId: Int,
    val category: QuestionCategory,
    val meaning: String,
    val symbol: String
)

// 解析後的時間範圍
data class ParsedTimeRange(
    val year: Int? = null,
    val startMonth: Int? = null,
    val endMonth: Int? = null
)
