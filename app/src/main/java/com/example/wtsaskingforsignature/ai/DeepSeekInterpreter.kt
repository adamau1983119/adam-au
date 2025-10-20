package com.example.wtsaskingforsignature.ai

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.data.EnhancedContext.PersonalizedResponse
import com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.UserProfile
import com.example.wtsaskingforsignature.data.EnhancedModels.FortuneMeaning
import com.example.wtsaskingforsignature.data.EnhancedModels.MonthlyAnalysis
import com.example.wtsaskingforsignature.modules.EnhancedResponseGenerator
import com.example.wtsaskingforsignature.modules.EnhancedZiweiSemanticMapper
import com.example.wtsaskingforsignature.modules.QuestionAnalyzer
import com.example.wtsaskingforsignature.modules.TimeWindowAnalyzer
import com.example.wtsaskingforsignature.modules.FortuneDataIntegrator
import com.example.wtsaskingforsignature.modules.ZiweiCalculatorIntegrator
import com.example.wtsaskingforsignature.util.WtsLogger

/**
 * 真正執行模組的DeepSeek解籤器
 * 整合App內現有功能，實際執行所有模組
 */
class DeepSeekInterpreter {

    // 真正的模組實例
    private val questionAnalyzer = QuestionAnalyzer()
    private val fortuneDataIntegrator = FortuneDataIntegrator()
    private val ziweiCalculatorIntegrator = ZiweiCalculatorIntegrator()
    private val timeWindowAnalyzer = TimeWindowAnalyzer()
    private val ziweiSemanticMapper = EnhancedZiweiSemanticMapper()
    private val responseGenerator = EnhancedResponseGenerator()

    suspend fun interpretFortune(
        question: String,
        userProfile: UserProfile,
        ziweiData: ZiweiAnalysis,
        fortuneId: Int
    ): PersonalizedResponse {
        WtsLogger.i("DeepSeekInterpreter: 開始解籤 - 問題=$question, 籤文ID=$fortuneId")
        
        var context = AnalysisContext(
            userProfile = userProfile,
            ziweiData = ziweiData,
            fortuneId = fortuneId,
            metadata = mutableMapOf("rawQuestion" to question)
        )

        try {
            // Step 1: 真正的問題分析
            WtsLogger.i("Step 1: 執行問題分析模組")
            val questionAnalysisResult = questionAnalyzer.process(context)
            if (!questionAnalysisResult.success) {
                WtsLogger.e("問題分析失敗: ${questionAnalysisResult.errorMessage}")
                return createFallbackResponse("問題分析失敗")
            }
            context = context.copy(question = questionAnalysisResult.data as? com.example.wtsaskingforsignature.data.EnhancedContext.QuestionAnalysis)

            // Step 2: 真正的籤文資料整合
            WtsLogger.i("Step 2: 執行籤文資料整合模組")
            val fortuneDataResult = fortuneDataIntegrator.process(context)
            if (!fortuneDataResult.success) {
                WtsLogger.e("籤文資料整合失敗: ${fortuneDataResult.errorMessage}")
                return createFallbackResponse("籤文資料整合失敗")
            }
            val fortuneMeaning = fortuneDataResult.data as? FortuneMeaning
            context = context.copy(metadata = context.metadata.apply { put("fortuneMeaning", fortuneMeaning) })

            // Step 3: 真正的紫微斗數計算
            WtsLogger.i("Step 3: 執行紫微斗數計算模組")
            val ziweiCalculationResult = ziweiCalculatorIntegrator.process(context)
            if (!ziweiCalculationResult.success) {
                WtsLogger.e("紫微斗數計算失敗: ${ziweiCalculationResult.errorMessage}")
                return createFallbackResponse("紫微斗數計算失敗")
            }
            context = context.copy(ziweiData = ziweiCalculationResult.data as? ZiweiAnalysis)

            // Step 4: 真正的時間分析
            WtsLogger.i("Step 4: 執行時間分析模組")
            val timeAnalysisResult = timeWindowAnalyzer.process(context)
            if (timeAnalysisResult.success) {
                context = context.copy(monthlyAnalysis = timeAnalysisResult.data as? MonthlyAnalysis)
            } else {
                WtsLogger.w("時間分析跳過或失敗: ${timeAnalysisResult.errorMessage}")
            }

            // Step 5: 真正的語意映射
            WtsLogger.i("Step 5: 執行語意映射模組")
            val semanticResult = ziweiSemanticMapper.process(context)
            if (!semanticResult.success) {
                WtsLogger.e("語意映射失敗: ${semanticResult.errorMessage}")
                return createFallbackResponse("語意映射失敗")
            }
            context = context.copy(fortuneConnection = semanticResult.data as? String)

            // Step 6: 真正的回答生成
            WtsLogger.i("Step 6: 執行回答生成模組")
            val finalResponseResult = responseGenerator.process(context)
            if (!finalResponseResult.success) {
                WtsLogger.e("回答生成失敗: ${finalResponseResult.errorMessage}")
                return createFallbackResponse("回答生成失敗")
            }

            val response = finalResponseResult.data as PersonalizedResponse
            WtsLogger.i("DeepSeekInterpreter: 解籤完成 - 回答長度=${response.coreInterpretation.length}")
            return response

        } catch (e: Exception) {
            WtsLogger.e("DeepSeekInterpreter 執行失敗: ${e.message}", e)
            return createFallbackResponse("系統執行失敗: ${e.message}")
        }
    }

    private fun createFallbackResponse(errorMessage: String): PersonalizedResponse {
        return PersonalizedResponse(
            coreInterpretation = "很抱歉，目前無法提供詳細解讀。$errorMessage",
            ziweiConnection = "",
            personalizedAdvice = listOf("請稍後再試或嘗試其他問題。"),
            timeGuidance = "",
            fortuneConnection = "",
            precautions = emptyList(),
            tone = com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone.GENTLE,
            confidence = 0.1f
        )
    }
}