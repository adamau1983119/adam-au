package com.example.wtsaskingforsignature.modules

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisModule
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.config.TerminologyConfig
import com.example.wtsaskingforsignature.config.MVPSemanticConfig
import com.example.wtsaskingforsignature.data.EnhancedModels.FortuneMeaning
import com.example.wtsaskingforsignature.util.WtsLogger
/**
 * MVP簡化的紫微語意映射模組
 * 使用關鍵詞匹配 + 籤文優先邏輯
 */
class EnhancedZiweiSemanticMapper : AnalysisModule {
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            WtsLogger.i("MVPSemanticMapper: 開始簡化語意映射")
            
            val professionalExplanation = generateSimplifiedExplanation(context)
            val fortuneIntegration = integrateFortuneWithPriority(context)
            
            val result = buildString {
                append(professionalExplanation)
                if (fortuneIntegration.isNotEmpty()) {
                    append("。$fortuneIntegration")
                }
            }
            
            WtsLogger.i("MVPSemanticMapper: 語意映射完成 - 長度=${result.length}")
            
            AnalysisResult(
                success = true,
                resultCode = "MVP_SEMANTIC_MAPPING_SUCCESS",
                data = result,
                processingTime = System.currentTimeMillis(),
                moduleName = "EnhancedZiweiSemanticMapper"
            )
        } catch (e: Exception) {
            WtsLogger.e("MVPSemanticMapper: 語意映射失敗 - ${e.message}", e)
            AnalysisResult(
                success = false,
                resultCode = "MVP_SEMANTIC_MAPPING_ERROR",
                errorMessage = e.message,
                processingTime = System.currentTimeMillis(),
                moduleName = "EnhancedZiweiSemanticMapper"
            )
        }
    }
    
    /**
     * 簡化的專業解釋生成
     * 使用關鍵詞匹配和基本邏輯
     */
    private fun generateSimplifiedExplanation(context: AnalysisContext): String {
        val ziweiData = context.ziweiData ?: return "命盤分析暫不可用"
        val category = context.question?.category ?: com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.GENERAL
        
        val explanations = mutableListOf<String>()
        
        // 根據問題類別選擇對應宮位
        val relevantPalace = when (category) {
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.CAREER -> ziweiData.careerPalace
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.LOVE -> ziweiData.lovePalace
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.HEALTH -> ziweiData.healthPalace
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.WEALTH -> ziweiData.wealthPalace
            else -> null
        }
        
        if (relevantPalace != null) {
            val siHua = relevantPalace.siHua
            val palaceName = getPalaceName(category)
            when {
                siHua.contains("祿") -> explanations.add("${palaceName}見吉星化祿，運勢亨通")
                siHua.contains("科") -> explanations.add("${palaceName}有貴人星輔助，利於發展")
                siHua.contains("忌") -> explanations.add("${palaceName}見化忌，需謹慎處理")
                else -> explanations.add("${palaceName}星曜配置平穩")
            }
        } else {
            // 如果沒有對應宮位，使用命宮
            val mingGong = ziweiData.mingGong
            if (mingGong != null) {
                val siHua = mingGong.siHua
                val palaceName = getPalaceName(category)
                when {
                    siHua.contains("祿") -> explanations.add("${palaceName}見吉星化祿，運勢亨通")
                    siHua.contains("科") -> explanations.add("${palaceName}有貴人星輔助，利於發展")
                    siHua.contains("忌") -> explanations.add("${palaceName}見化忌，需謹慎處理")
                    else -> explanations.add("${palaceName}星曜配置平穩")
                }
            }
        }
        
        val baseExplanation = explanations.joinToString("，")
        return if (baseExplanation.isNotEmpty()) {
            "$baseExplanation，顯示${getCategoryDescription(category)}運勢漸入佳境"
        } else {
            "命盤顯示${getCategoryDescription(category)}運勢平穩"
        }
    }
    
    /**
     * 籤文優先的整合邏輯
     * 實現「籤文為主，命盤為輔」的固定邏輯
     */
    private fun integrateFortuneWithPriority(context: AnalysisContext): String {
        val fortuneId = context.fortuneId ?: return ""
        val category = context.question?.category ?: com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.GENERAL
        
        // 獲取籤文內容
        val fortuneMeaning = context.metadata["fortuneMeaning"] as? FortuneMeaning
        if (fortuneMeaning == null) {
            WtsLogger.w("MVPSemanticMapper: 籤文內容不可用")
            return ""
        }
        
        // 從籤文內容中提取主題
        val fortuneTheme = MVPSemanticConfig.extractThemeFromFortune(fortuneMeaning.content)
        val ziweiAnalysis = context.ziweiData?.let { generateSimplifiedExplanation(context) } ?: ""
        
        // 檢測衝突
        val hasConflict = MVPSemanticConfig.hasConflict(fortuneTheme, ziweiAnalysis)
        val priorityPhrase = MVPSemanticConfig.getFortunePriorityPhrase(hasConflict, "positive")
        
        return buildString {
            append("第${fortuneId}籤寓意「${fortuneMeaning.title}」，")
            append("籤文顯示${getCategoryDescription(category)}運勢${getFortuneSentiment(fortuneMeaning.content)}。")
            if (hasConflict) {
                append("$priorityPhrase")
            }
        }
    }
    
    private fun getPalaceName(category: com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory): String {
        return when (category) {
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.CAREER -> "官祿宮"
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.LOVE -> "夫妻宮"
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.HEALTH -> "疾厄宮"
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.WEALTH -> "財帛宮"
            else -> "命宮"
        }
    }
    
    private fun getCategoryDescription(category: com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory): String {
        return when (category) {
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.CAREER -> "事業"
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.LOVE -> "感情"
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.HEALTH -> "健康"
            com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory.WEALTH -> "財運"
            else -> "整體"
        }
    }
    
    private fun getFortuneSentiment(content: String): String {
        val positiveKeywords = listOf("吉", "好", "成", "利", "順", "旺", "興", "發", "上上", "中上")
        val negativeKeywords = listOf("凶", "壞", "敗", "不利", "逆", "衰", "困", "破", "下下", "中下")
        
        return when {
            positiveKeywords.any { content.contains(it) } -> "積極向上"
            negativeKeywords.any { content.contains(it) } -> "需要謹慎"
            else -> "平穩發展"
        }
    }
}
