package com.wongtaisim.lingqian.data.model

/**
 * 籤文資料模型
 */
data class QianData(
    val number: Int,
    val title: String,
    val content: String,
    val summary: String,
    val category: QianCategory,
    val level: QianLevel,
    val interpretation: String
)

/**
 * 籤文類別
 */
enum class QianCategory(val displayName: String) {
    CAREER("事業"),
    WEALTH("財運"),
    SELF("自身"),
    FAMILY("家庭"),
    MARRIAGE("婚姻"),
    MIGRATION("遷徙"),
    REPUTATION("名譽"),
    HEALTH("健康"),
    FRIENDSHIP("友誼"),
    OTHER("其他")
}

/**
 * 籤文等級
 */
enum class QianLevel(val displayName: String, val color: String) {
    EXCELLENT("上上籤", "#4CAF50"),
    GOOD("上籤", "#8BC34A"),
    AVERAGE("中籤", "#FF9800"),
    POOR("下籤", "#FF5722"),
    BAD("下下籤", "#F44336")
}

/**
 * 擲杯結果
 */
data class CupResult(
    val throwNumber: Int,
    val result: CupSide,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 擲杯面
 */
enum class CupSide(val displayName: String) {
    POSITIVE("正"),
    NEGATIVE("反")
}

/**
 * 擲杯驗證結果
 */
data class CupValidationResult(
    val isValid: Boolean,
    val positiveCount: Int,
    val negativeCount: Int,
    val message: String
)

/**
 * 抽籤請求
 */
data class DrawQianRequest(
    val category: QianCategory,
    val userId: String? = null,
    val integrityToken: String? = null
)

/**
 * 抽籤回應
 */
data class DrawQianResponse(
    val success: Boolean,
    val qianData: QianData? = null,
    val message: String,
    val dailyLimitReached: Boolean = false
)

/**
 * 每日一籤狀態
 */
data class DailyQianStatus(
    val hasDrawnToday: Boolean,
    val lastDrawTime: Long? = null,
    val qianData: QianData? = null
)

/**
 * 對話請求
 */
data class ChatRequest(
    val question: String,
    val qianData: QianData,
    val userId: String? = null
)

/**
 * 對話回應
 */
data class ChatResponse(
    val success: Boolean,
    val answer: String? = null,
    val message: String
)
