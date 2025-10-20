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
