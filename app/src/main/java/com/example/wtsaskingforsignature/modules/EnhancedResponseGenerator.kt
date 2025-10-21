package com.example.wtsaskingforsignature.modules

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisModule
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.data.EnhancedContext.PersonalizedResponse
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionIntent
import com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone
import com.example.wtsaskingforsignature.data.EnhancedModels.MonthlyAnalysis
// 增強的回答生成模組
class EnhancedResponseGenerator : AnalysisModule {
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            val professionalExplanation = context.semanticExplanation as? String ?: ""
            val timeAnalysis = context.monthlyAnalysis as? MonthlyAnalysis
            val fortuneIntegration = context.fortuneIntegration as? String ?: ""
            val practicalAdvice = generatePracticalAdvice(context)
            
            val response = buildString {
                // 1. 命理解釋 + 籤文寓意（Google AI風格）
                append(professionalExplanation)
                if (fortuneIntegration.isNotEmpty()) {
                    append("。").append(fortuneIntegration)
                }
                
                // 2. 時間分析
                if (timeAnalysis != null) {
                    append("。")
                    append(generateTimeAnalysisText(timeAnalysis))
                }
                
                // 3. 綜合結論
                append("。整體而言，年底事業運漸入佳境，宜耐心布局，把握時機。")
            }
            
            val personalizedResponse = PersonalizedResponse(
                coreInterpretation = professionalExplanation,
                ziweiConnection = professionalExplanation,
                personalizedAdvice = listOf(practicalAdvice),
                timeGuidance = generateTimeAnalysisText(timeAnalysis),
                fortuneConnection = fortuneIntegration,
                precautions = generatePrecautions(context),
                tone = selectTone(context),
                confidence = calculateConfidence(context)
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
            if (monthlyAnalysis.october.isNotEmpty()) {
                append("10月${monthlyAnalysis.october}")
            }
            if (monthlyAnalysis.november.isNotEmpty()) {
                if (isNotEmpty()) append("；")
                append("11月${monthlyAnalysis.november}")
            }
            if (monthlyAnalysis.december.isNotEmpty()) {
                if (isNotEmpty()) append("；")
                append("12月${monthlyAnalysis.december}")
            }
        }
    }
    
    private fun generatePracticalAdvice(context: AnalysisContext): String {
        val isLove = context.question?.category == QuestionCategory.LOVE
        return if (isLove) {
            when (context.question?.intent) {
                QuestionIntent.PREDICTION -> "把握社交契機，留意年底緣分出現的場合"
                QuestionIntent.ADVICE -> "擴大交友圈、提升互動頻率，真誠表達與傾聽"
                QuestionIntent.WARNING -> "避免急於定論，先理解彼此期待與界線"
                else -> "調整作息與心態，經營能量，邂逅自然會來"
            }
        } else {
            when (context.question?.intent) {
                QuestionIntent.PREDICTION -> "提前布局，穩中求進，把握年底轉機"
                QuestionIntent.ADVICE -> "保持專業態度，積極爭取機會"
                QuestionIntent.WARNING -> "謹慎決策，避免衝動"
                else -> "穩步發展，把握機會"
            }
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
