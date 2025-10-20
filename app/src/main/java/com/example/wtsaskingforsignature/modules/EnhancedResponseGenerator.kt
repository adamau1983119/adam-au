// 增強的回答生成模組
class EnhancedResponseGenerator : AnalysisModule {
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            val professionalExplanation = context.semanticExplanation as? String ?: ""
            val timeAnalysis = context.monthlyAnalysis as? MonthlyAnalysis
            val fortuneIntegration = context.fortuneIntegration as? String ?: ""
            val practicalAdvice = generatePracticalAdvice(context)
            
            val response = buildString {
                append(professionalExplanation)
                if (timeAnalysis != null) {
                    append("\n\n")
                    append(generateTimeAnalysisText(timeAnalysis))
                }
                if (fortuneIntegration.isNotEmpty()) {
                    append("\n\n")
                    append(fortuneIntegration)
                }
                append("\n\n")
                append("建議：$practicalAdvice")
            }
            
            val personalizedResponse = PersonalizedResponse(
                coreInterpretation = professionalExplanation,
                ziweiConnection = professionalExplanation,
                personalizedAdvice = listOf(practicalAdvice),
                timeGuidance = generateTimeAnalysisText(timeAnalysis),
                fortuneConnection = fortuneIntegration,
                precautions = generatePrecautions(context),
                tone = selectTone(context),
                confidence = calculateConfidence(context),
                professionalLevel = 0.9f
            )
            
            AnalysisResult(
                success = true,
                resultCode = "RESPONSE_GENERATION_SUCCESS",
                data = personalizedResponse,
                processingTime = System.currentTimeMillis(),
                moduleName = "EnhancedResponseGenerator"
            )
        } catch (e: Exception) {
            AnalysisResult(
                success = false,
                resultCode = "RESPONSE_GENERATION_ERROR",
                errorMessage = e.message,
                processingTime = System.currentTimeMillis(),
                moduleName = "EnhancedResponseGenerator"
            )
        }
    }
    
    private fun generateTimeAnalysisText(monthlyAnalysis: MonthlyAnalysis?): String {
        if (monthlyAnalysis == null) return ""
        
        return buildString {
            append("時間分析：\n")
            if (monthlyAnalysis.october.isNotEmpty()) {
                append("- 10月：${monthlyAnalysis.october}\n")
            }
            if (monthlyAnalysis.november.isNotEmpty()) {
                append("- 11月：${monthlyAnalysis.november}\n")
            }
            if (monthlyAnalysis.december.isNotEmpty()) {
                append("- 12月：${monthlyAnalysis.december}")
            }
        }
    }
    
    private fun generatePracticalAdvice(context: AnalysisContext): String {
        return when (context.question?.intent) {
            QuestionIntent.PREDICTION -> "提前布局，穩中求進，把握年底轉機"
            QuestionIntent.ADVICE -> "保持專業態度，積極爭取機會"
            QuestionIntent.WARNING -> "謹慎決策，避免衝動"
            else -> "穩步發展，把握機會"
        }
    }
    
    private fun generatePrecautions(context: AnalysisContext): List<String> {
        val precautions = mutableListOf<String>()
        
        when (context.question?.category) {
            QuestionCategory.CAREER -> {
                precautions.add("避免同時跳槽與創業")
                precautions.add("避免衝動簽長約")
            }
            QuestionCategory.LOVE -> {
                precautions.add("避免過於急躁")
                precautions.add("注意溝通方式")
            }
            else -> {
                precautions.add("保持理性思考")
                precautions.add("避免衝動決策")
            }
        }
        
        return precautions
    }
    
    private fun selectTone(context: AnalysisContext): ResponseTone {
        val userProfile = context.userProfile
        return when {
            userProfile?.age ?: 0 < 30 -> ResponseTone.ENCOURAGING
            userProfile?.age ?: 0 > 50 -> ResponseTone.WARM
            context.question?.category == QuestionCategory.CAREER -> ResponseTone.PROFESSIONAL
            else -> ResponseTone.DIRECT
        }
    }
    
    private fun calculateConfidence(context: AnalysisContext): Float {
        var confidence = 0.5f
        
        // 基於命盤數據完整性
        if (context.ziweiData?.careerPalace != null) confidence += 0.2f
        if (context.question?.timeRange?.isNotEmpty() == true) confidence += 0.2f
        if (context.fortuneId != null) confidence += 0.1f
        
        return minOf(confidence, 1.0f)
    }
}
