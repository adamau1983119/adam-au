package com.example.wtsaskingforsignature.modules

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisModule
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionAnalysis
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionIntent
import com.example.wtsaskingforsignature.data.EnhancedContext.SentimentScore
import com.example.wtsaskingforsignature.data.EnhancedContext.UrgencyLevel
import com.example.wtsaskingforsignature.config.MVPSemanticConfig
import com.example.wtsaskingforsignature.util.WtsLogger

/**
 * 真正的問題分析模組
 * 整合現有App功能，實際執行問題分析
 */
class QuestionAnalyzer : AnalysisModule {
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            val rawQuestion = context.metadata["rawQuestion"] as? String ?: ""
            WtsLogger.i("QuestionAnalyzer: 分析問題 = $rawQuestion")
            
            val questionAnalysis = analyzeQuestion(rawQuestion)
            
            AnalysisResult(
                success = true,
                resultCode = "SUCCESS",
                data = questionAnalysis,
                moduleName = getModuleName()
            )
        } catch (e: Exception) {
            WtsLogger.e("QuestionAnalyzer 執行失敗: ${e.message}", e)
            AnalysisResult(
                success = false,
                resultCode = "ANALYSIS_FAILED",
                errorMessage = e.message,
                moduleName = getModuleName()
            )
        }
    }
    
    override fun getModuleName(): String = "QuestionAnalyzer"
    override fun getVersion(): String = "1.0"
    
    /**
     * 真正的問題分析邏輯
     */
    private fun analyzeQuestion(question: String): QuestionAnalysis {
        // 0. 解析【所求】標籤；若存在則優先使用
        val preset = parsePresetCategory(question)
        // 1. 問題分類
        val category = preset ?: analyzeQuestionCategory(question)
        
        // 2. 意圖識別
        val intent = analyzeQuestionIntent(question)
        
        // 3. 時間範圍提取
        val timeRange = extractTimeRange(question)
        
        // 4. 情感分析
        val sentiment = analyzeSentiment(question)
        
        // 5. 緊急程度評估
        val urgency = analyzeUrgency(question)
        
        return QuestionAnalysis(
            category = category,
            intent = intent,
            timeRange = timeRange,
            emotion = sentiment,
            urgency = urgency
        )
    }
    
    /**
     * 從富文本中解析預設分類，例如：『【所求】愛情』
     */
    private fun parsePresetCategory(question: String): QuestionCategory? {
        val line = question.lines().firstOrNull { it.startsWith("【所求】") } ?: return null
        val value = line.removePrefix("【所求】").trim()
        return when (value) {
            "事業", "career", "CAREER" -> QuestionCategory.CAREER
            "愛情", "感情", "love", "LOVE" -> QuestionCategory.LOVE
            "健康", "health", "HEALTH" -> QuestionCategory.HEALTH
            "財運", "money", "wealth", "WEALTH" -> QuestionCategory.WEALTH
            else -> null
        }
    }
    
    /**
     * MVP簡化的問題分類 - 使用關鍵詞匹配表
     */
    private fun analyzeQuestionCategory(question: String): QuestionCategory {
        WtsLogger.i("MVPQuestionAnalyzer: 使用關鍵詞匹配表分析問題分類")
        return MVPSemanticConfig.matchCategory(question)
    }
    
    /**
     * 意圖識別
     */
    private fun analyzeQuestionIntent(question: String): QuestionIntent {
        val lowerQuestion = question.lowercase()
        
        return when {
            lowerQuestion.contains("如何") || lowerQuestion.contains("怎樣") ||
            lowerQuestion.contains("怎麼辦") || lowerQuestion.contains("建議") -> QuestionIntent.ADVICE
            
            lowerQuestion.contains("會") || lowerQuestion.contains("將") ||
            lowerQuestion.contains("未來") || lowerQuestion.contains("預測") -> QuestionIntent.PREDICTION
            
            lowerQuestion.contains("注意") || lowerQuestion.contains("小心") ||
            lowerQuestion.contains("風險") || lowerQuestion.contains("避免") -> QuestionIntent.WARNING
            
            lowerQuestion.contains("是否") || lowerQuestion.contains("對嗎") ||
            lowerQuestion.contains("確認") -> QuestionIntent.CONFIRMATION
            
            else -> QuestionIntent.EXPLANATION
        }
    }
    
    /**
     * 時間範圍提取
     */
    private fun extractTimeRange(question: String): String {
        val patterns = listOf(
            "2025年" to "2025年",
            "2024年" to "2024年", 
            "2026年" to "2026年",
            "今年" to "2025年",
            "明年" to "2026年",
            "10-12月" to "2025年10-12月",
            "10至12月" to "2025年10-12月",
            "年底" to "2025年年底",
            "近期" to "近期",
            "短期" to "短期",
            "長期" to "長期"
        )
        
        for ((pattern, result) in patterns) {
            if (question.contains(pattern)) {
                return result
            }
        }
        
        return "一般時期"
    }
    
    /**
     * 情感分析
     */
    private fun analyzeSentiment(question: String): SentimentScore {
        val lowerQuestion = question.lowercase()
        
        val positiveWords = listOf("好", "順利", "成功", "開心", "快樂", "希望", "期待")
        val negativeWords = listOf("不好", "困難", "問題", "擔心", "焦慮", "煩惱", "困擾")
        
        val positiveCount = positiveWords.count { lowerQuestion.contains(it) }
        val negativeCount = negativeWords.count { lowerQuestion.contains(it) }
        
        return when {
            positiveCount > negativeCount -> SentimentScore.POSITIVE
            negativeCount > positiveCount -> SentimentScore.NEGATIVE
            else -> SentimentScore.NEUTRAL
        }
    }
    
    /**
     * 緊急程度評估
     */
    private fun analyzeUrgency(question: String): UrgencyLevel {
        val lowerQuestion = question.lowercase()
        
        return when {
            lowerQuestion.contains("急") || lowerQuestion.contains("緊急") ||
            lowerQuestion.contains("立即") || lowerQuestion.contains("馬上") -> UrgencyLevel.HIGH
            
            lowerQuestion.contains("盡快") || lowerQuestion.contains("快點") ||
            lowerQuestion.contains("趕快") -> UrgencyLevel.MEDIUM
            
            else -> UrgencyLevel.LOW
        }
    }
}
