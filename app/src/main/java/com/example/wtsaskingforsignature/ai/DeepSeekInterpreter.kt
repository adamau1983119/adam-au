package com.example.wtsaskingforsignature.ai

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.data.EnhancedContext.PersonalizedResponse
import com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone
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
import com.example.wtsaskingforsignature.modules.StandardTemplateGenerator
import com.example.wtsaskingforsignature.util.WtsLogger
import com.example.wtsaskingforsignature.data.memory.InteractionStore
import com.example.wtsaskingforsignature.utils.ReverseExtractionTool

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
    private val standardTemplateGenerator = StandardTemplateGenerator()

    suspend fun interpretFortune(
        question: String,
        userProfile: UserProfile,
        ziweiData: ZiweiAnalysis,
        fortuneId: Int
    ): PersonalizedResponse {
        WtsLogger.i("DeepSeekExecution: 開始解籤 - 問題=$question, 籤文ID=$fortuneId")

        var context = AnalysisContext(
            userProfile = userProfile,
            ziweiData = ziweiData,
            fortuneId = fortuneId,
            metadata = mutableMapOf("rawQuestion" to question)
        )

        try {
            // Step 1: 真正的問題分析
            WtsLogger.i("DeepSeekExecution: Step 1 問題分析模組執行")
            val questionAnalysisResult = try { questionAnalyzer.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 問題分析異常: ${e.message}", e)
                return createFallbackResponse("問題分析失敗")
            }
            if (!questionAnalysisResult.success) {
                WtsLogger.e("DeepSeekExecution: 問題分析失敗: ${questionAnalysisResult.errorMessage}")
                return createFallbackResponse("問題分析失敗")
            }
            context = context.copy(question = questionAnalysisResult.data as? com.example.wtsaskingforsignature.data.EnhancedContext.QuestionAnalysis)
            WtsLogger.d("DeepSeekExecution: 問題分析完成 -> ${context.question}")

            // Step 2: 真正的籤文資料整合
            WtsLogger.i("DeepSeekExecution: Step 2 籤文資料整合模組執行")
            val fortuneDataResult = try { fortuneDataIntegrator.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 籤文資料整合異常: ${e.message}", e)
                return createFallbackResponse("籤文資料整合失敗")
            }
            if (!fortuneDataResult.success) {
                WtsLogger.e("DeepSeekExecution: 籤文資料整合失敗: ${fortuneDataResult.errorMessage}")
                return createFallbackResponse("籤文資料整合失敗")
            }
            val fortuneMeaning = fortuneDataResult.data as? FortuneMeaning
            context = context.copy(metadata = context.metadata.apply { this["fortuneMeaning"] = fortuneMeaning })
            WtsLogger.d("DeepSeekExecution: 籤文整合完成 -> ${fortuneDataResult.metadata}")

            // Step 3: 真正的紫微斗數計算
            WtsLogger.i("DeepSeekExecution: Step 3 紫微斗數計算模組執行")
            val ziweiCalculationResult = try { ziweiCalculatorIntegrator.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 紫微斗數計算異常: ${e.message}", e)
                return createFallbackResponse("紫微斗數計算失敗")
            }
            if (!ziweiCalculationResult.success) {
                WtsLogger.e("DeepSeekExecution: 紫微斗數計算失敗: ${ziweiCalculationResult.errorMessage}")
                return createFallbackResponse("紫微斗數計算失敗")
            }
            context = context.copy(ziweiData = ziweiCalculationResult.data as? ZiweiAnalysis)
            WtsLogger.d("DeepSeekExecution: 紫微斗數計算完成 -> ${context.ziweiData}")

            // Step 4: 真正的時間窗口分析
            WtsLogger.i("DeepSeekExecution: Step 4 時間窗口分析模組執行")
            val timeWindowResult = try { timeWindowAnalyzer.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 時間窗口分析異常: ${e.message}", e)
                return createFallbackResponse("時間窗口分析失敗")
            }
            if (!timeWindowResult.success) {
                WtsLogger.e("DeepSeekExecution: 時間窗口分析失敗: ${timeWindowResult.errorMessage}")
                return createFallbackResponse("時間窗口分析失敗")
            }
            val monthlyAnalysis = timeWindowResult.data as? MonthlyAnalysis
            context = context.copy(metadata = context.metadata.apply { this["monthlyAnalysis"] = monthlyAnalysis })
            WtsLogger.d("DeepSeekExecution: 時間窗口分析完成 -> ${timeWindowResult.metadata}")

            // Step 5: 真正的語義映射
            WtsLogger.i("DeepSeekExecution: Step 5 語義映射模組執行")
            val semanticMappingResult = try { ziweiSemanticMapper.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 語義映射異常: ${e.message}", e)
                return createFallbackResponse("語義映射失敗")
            }
            if (!semanticMappingResult.success) {
                WtsLogger.e("DeepSeekExecution: 語義映射失敗: ${semanticMappingResult.errorMessage}")
                return createFallbackResponse("語義映射失敗")
            }
            context = context.copy(metadata = context.metadata.apply { this["semanticMapping"] = semanticMappingResult.data })
            WtsLogger.d("DeepSeekExecution: 語義映射完成 -> ${semanticMappingResult.metadata}")

            // Step 6: 真正的回答生成
            WtsLogger.i("DeepSeekExecution: Step 6 回答生成模組執行")
            val responseGenerationResult = try { responseGenerator.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 回答生成異常: ${e.message}", e)
                return createFallbackResponse("回答生成失敗")
            }
            if (!responseGenerationResult.success) {
                WtsLogger.e("DeepSeekExecution: 回答生成失敗: ${responseGenerationResult.errorMessage}")
                return createFallbackResponse("回答生成失敗")
            }
            val personalizedResponse = responseGenerationResult.data as? PersonalizedResponse
            WtsLogger.d("DeepSeekExecution: 回答生成完成 -> ${responseGenerationResult.metadata}")

            // Step 7: 最終的標準模板生成
            WtsLogger.i("DeepSeekExecution: Step 7 標準模板生成模組執行")
            val templateGenerationResult = try { standardTemplateGenerator.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 標準模板生成異常: ${e.message}", e)
                return createFallbackResponse("標準模板生成失敗")
            }
            if (!templateGenerationResult.success) {
                WtsLogger.e("DeepSeekExecution: 標準模板生成失敗: ${templateGenerationResult.errorMessage}")
                return createFallbackResponse("標準模板生成失敗")
            }
            val finalResponse = templateGenerationResult.data as? PersonalizedResponse
            WtsLogger.d("DeepSeekExecution: 標準模板生成完成 -> ${templateGenerationResult.metadata}")

            // 返回最終結果
            val finalResult = finalResponse ?: personalizedResponse ?: createFallbackResponse("無法生成最終回答")
            WtsLogger.i("DeepSeekExecution: 解籤完成 - 成功生成個性化回答")
            return finalResult

        } catch (e: Exception) {
            WtsLogger.e("DeepSeekExecution: 解籤過程發生未預期異常: ${e.message}", e)
            return createFallbackResponse("解籤過程發生異常")
        }
    }

    /**
     * 創建降級回答
     */
    private fun createFallbackResponse(reason: String): PersonalizedResponse {
        WtsLogger.w("DeepSeekExecution: 創建降級回答 - 原因: $reason")
        return PersonalizedResponse(
            coreInterpretation = "由於系統暫時無法提供詳細分析，請稍後再試。原因：$reason",
            ziweiConnection = "命盤分析服務暫時不可用",
            personalizedAdvice = listOf("請稍後重新嘗試", "或聯繫客服協助"),
            timeGuidance = "建議稍後再試",
            fortuneConnection = "籤文與命盤結合分析",
            precautions = listOf("系統維護中"),
            tone = ResponseTone.GENTLE,
            confidence = 0.1f
        )
    }
}