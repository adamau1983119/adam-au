// 整合的DeepSeek解籤器
class DeepSeekInterpreter {
    private val questionAnalyzer = QuestionAnalyzer()
    private val ziweiMapper = QuestionToZiweiMapper()
    private val timeWindowAnalyzer = TimeWindowAnalyzer()
    private val semanticMapper = EnhancedZiweiSemanticMapper()
    private val responseGenerator = EnhancedResponseGenerator()
    private val errorHandler = ModuleErrorHandler()
    
    suspend fun interpretFortune(
        question: String,
        userProfile: UserProfile,
        ziweiData: ZiweiAnalysis,
        fortuneId: Int
    ): PersonalizedResponse {
        val traceId = generateTraceId()
        var context = EnhancedAnalysisContext(
            traceId = traceId,
            question = null,
            ziweiData = ziweiData,
            response = null,
            monthlyAnalysis = null,
            semanticExplanation = null,
            fortuneIntegration = null,
            fortuneId = fortuneId,
            userProfile = userProfile
        )
        
        try {
            // 1. 問題分析
            context = processQuestionAnalysis(context, question)
            
            // 2. 命盤映射
            context = processZiweiMapping(context)
            
            // 3. 時間分析
            context = processTimeAnalysis(context)
            
            // 4. 語意生成
            context = processSemanticMapping(context)
            
            // 5. 回答生成
            val result = responseGenerator.process(context)
            
            return if (result.success) {
                result.data as PersonalizedResponse
            } else {
                createFallbackResponse(context)
            }
            
        } catch (e: Exception) {
            WtsLogger.e("DeepSeekInterpreter failed: ${e.message}", e)
            return createFallbackResponse(context)
        }
    }
    
    private suspend fun processQuestionAnalysis(
        context: EnhancedAnalysisContext,
        question: String
    ): EnhancedAnalysisContext {
        val questionAnalysis = QuestionAnalysis(
            category = analyzeQuestionCategory(question),
            intent = analyzeQuestionIntent(question),
            timeRange = extractTimeRange(question),
            emotion = SentimentScore.NEUTRAL,
            urgency = UrgencyLevel.MEDIUM
        )
        
        return context.copy(question = questionAnalysis)
    }
    
    private suspend fun processZiweiMapping(context: EnhancedAnalysisContext): EnhancedAnalysisContext {
        // 基於問題類型觸發對應的命盤分析
        return context
    }
    
    private suspend fun processTimeAnalysis(context: EnhancedAnalysisContext): EnhancedAnalysisContext {
        val result = timeWindowAnalyzer.process(context)
        val monthlyAnalysis = if (result.success) {
            result.data as? MonthlyAnalysis
        } else {
            null
        }
        
        return context.copy(monthlyAnalysis = monthlyAnalysis)
    }
    
    private suspend fun processSemanticMapping(context: EnhancedAnalysisContext): EnhancedAnalysisContext {
        val result = semanticMapper.process(context)
        val semanticExplanation = if (result.success) {
            result.data as? String
        } else {
            null
        }
        
        val fortuneIntegration = generateFortuneIntegration(context)
        
        return context.copy(
            semanticExplanation = semanticExplanation,
            fortuneIntegration = fortuneIntegration
        )
    }
    
    private fun generateFortuneIntegration(context: EnhancedAnalysisContext): String {
        val fortuneId = context.fortuneId ?: return ""
        val category = context.question?.category ?: return ""
        
        val fortuneMeaning = TerminologyConfig.fortuneMeanings[fortuneId]?.get(category)
        return fortuneMeaning?.let { meaning ->
            "黃大仙第${fortuneId}籤寓意「${meaning.meaning}」，象徵${meaning.symbol}"
        } ?: ""
    }
    
    private fun createFallbackResponse(context: EnhancedAnalysisContext): PersonalizedResponse {
        return PersonalizedResponse(
            coreInterpretation = "根據您的命盤分析，整體運勢平穩",
            ziweiConnection = "命盤顯示運勢良好",
            personalizedAdvice = listOf("保持積極態度，把握機會"),
            timeGuidance = "穩步發展，把握時機",
            fortuneConnection = "籤文顯示運勢向好",
            precautions = listOf("保持理性思考"),
            tone = ResponseTone.PROFESSIONAL,
            confidence = 0.6f,
            professionalLevel = 0.7f
        )
    }
    
    private fun generateTraceId(): String {
        return "trace_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    // 輔助方法
    private fun analyzeQuestionCategory(question: String): QuestionCategory {
        return when {
            question.contains("事業") || question.contains("工作") || question.contains("職場") -> QuestionCategory.CAREER
            question.contains("感情") || question.contains("婚姻") || question.contains("愛情") -> QuestionCategory.LOVE
            question.contains("健康") || question.contains("身體") || question.contains("疾病") -> QuestionCategory.HEALTH
            question.contains("財運") || question.contains("金錢") || question.contains("投資") -> QuestionCategory.WEALTH
            else -> QuestionCategory.GENERAL
        }
    }
    
    private fun analyzeQuestionIntent(question: String): QuestionIntent {
        return when {
            question.contains("如何") || question.contains("怎樣") -> QuestionIntent.ADVICE
            question.contains("預測") || question.contains("未來") -> QuestionIntent.PREDICTION
            question.contains("注意") || question.contains("小心") -> QuestionIntent.WARNING
            else -> QuestionIntent.ADVICE
        }
    }
    
    private fun extractTimeRange(question: String): String {
        val timePattern = Regex("(\\d{4}年)?(\\d{1,2}[-至]\\d{1,2}月|年底|明年|今年)")
        val match = timePattern.find(question)
        return match?.value ?: ""
    }
}
