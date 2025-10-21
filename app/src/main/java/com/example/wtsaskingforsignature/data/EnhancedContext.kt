package com.example.wtsaskingforsignature.data.EnhancedContext

// 核心資料契約與模組介面（最小可行版，供各模組對齊）

// 問題分類
enum class QuestionCategory { CAREER, LOVE, HEALTH, WEALTH, GENERAL }

// 用戶意圖
enum class QuestionIntent { PREDICTION, ADVICE, WARNING, CONFIRMATION, EXPLANATION }

// 情緒與緊急度
enum class SentimentScore { POSITIVE, NEGATIVE, NEUTRAL }
enum class UrgencyLevel { LOW, MEDIUM, HIGH }

// 回覆語氣
enum class ResponseTone { ENCOURAGING, WARM, PROFESSIONAL, DIRECT, GENTLE }

// 基本用戶屬性（供部分檔案直接引用）
enum class Gender { MALE, FEMALE, UNKNOWN }

// 問題分析結果
data class QuestionAnalysis(
    val category: QuestionCategory = QuestionCategory.GENERAL,
    val intent: QuestionIntent = QuestionIntent.EXPLANATION,
    val timeRange: String = "",
    val emotion: SentimentScore = SentimentScore.NEUTRAL,
    val urgency: UrgencyLevel = UrgencyLevel.MEDIUM,
)

// 前置宣告（由 EnhancedModels.kt 實作）
typealias ZiweiAnalysis = com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis
typealias UserProfile = com.example.wtsaskingforsignature.data.EnhancedModels.UserProfile
typealias MonthlyAnalysis = com.example.wtsaskingforsignature.data.EnhancedModels.MonthlyAnalysis

// 統一的分析上下文
data class AnalysisContext(
    val traceId: String = System.currentTimeMillis().toString(),
    val question: QuestionAnalysis? = null,
    val ziweiData: ZiweiAnalysis? = null,
    val response: PersonalizedResponse? = null,
    val monthlyAnalysis: MonthlyAnalysis? = null,
    val semanticExplanation: String? = null,
    val fortuneIntegration: String? = null,
    val fortuneId: Int? = null,
    val userProfile: UserProfile? = null,
    val version: Int = 1,
    val timestamps: Map<String, Long> = emptyMap(),
    val source: String = "user_input",
    val metadata: MutableMap<String, Any?> = mutableMapOf()
)

// 模組執行結果
data class AnalysisResult(
    val success: Boolean,
    val resultCode: String,
    val data: Any? = null,
    val errorMessage: String? = null,
    val processingTime: Long = System.currentTimeMillis(),
    val moduleName: String,
    val metadata: Map<String, Any>? = null
)

// 模組介面
interface AnalysisModule {
    fun process(context: AnalysisContext): AnalysisResult
    fun getModuleName(): String = this::class.simpleName ?: "Module"
    fun getVersion(): String = "1.0"
}

// 對外輸出的結構化回應
data class PersonalizedResponse(
    val coreInterpretation: String,
    val ziweiConnection: String,
    val personalizedAdvice: List<String>,
    val timeGuidance: String,
    val fortuneConnection: String,
    val precautions: List<String>,
    val tone: ResponseTone,
    val confidence: Float,
    val professionalLevel: Float = 0.8f
)

// 增強的AnalysisContext
data class EnhancedAnalysisContext(
    val traceId: String,
    val question: QuestionAnalysis?,
    val ziweiData: ZiweiAnalysis?,
    val response: PersonalizedResponse?,
    val monthlyAnalysis: MonthlyAnalysis?, // 新增
    val semanticExplanation: String?, // 新增
    val fortuneIntegration: String?, // 新增
    val fortuneId: Int?, // 新增
    val userProfile: UserProfile?, // 新增
    val version: Int = 1,
    val timestamps: Map<String, Long> = emptyMap(),
    val source: String = "user_input",
    val metadata: Map<String, Any> = emptyMap()
)

// 增強的PersonalizedResponse
data class EnhancedPersonalizedResponse(
    val coreInterpretation: String,
    val ziweiConnection: String,
    val personalizedAdvice: List<String>,
    val timeGuidance: String, // 新增：時間分析
    val fortuneConnection: String, // 新增：籤文寓意
    val precautions: List<String>,
    val tone: ResponseTone,
    val confidence: Float,
    val professionalLevel: Float = 0.8f // 新增：專業程度評分
)
