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

            // Step 4: 真正的時間分析
            WtsLogger.i("DeepSeekExecution: Step 4 時間分析模組執行")
            val timeAnalysisResult = try { timeWindowAnalyzer.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 時間分析異常: ${e.message}", e)
                null
            }
            if (timeAnalysisResult != null) {
                if (timeAnalysisResult.success) {
                    context = context.copy(monthlyAnalysis = timeAnalysisResult.data as? MonthlyAnalysis)
                    WtsLogger.d("DeepSeekExecution: 時間分析完成 -> ${context.monthlyAnalysis}")
                } else {
                    WtsLogger.w("DeepSeekExecution: 時間分析跳過/失敗: ${timeAnalysisResult.errorMessage}")
                }
            }

            // Step 5: 真正的語意映射
            WtsLogger.i("DeepSeekExecution: Step 5 語意映射模組執行")
            val semanticResult = try { ziweiSemanticMapper.process(context) } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 語意映射異常: ${e.message}", e)
                return createFallbackResponse("語意映射失敗")
            }
            if (!semanticResult.success) {
                WtsLogger.e("DeepSeekExecution: 語意映射失敗: ${semanticResult.errorMessage}")
                return createFallbackResponse("語意映射失敗")
            }
            context = context.copy(
                semanticExplanation = semanticResult.data as? String,
                fortuneIntegration = semanticResult.metadata?.get("fortuneIntegration") as? String
            )
            WtsLogger.d("DeepSeekExecution: 語意映射完成 -> ${context.semanticExplanation}")

            // Step 6: 標準範本回答生成
            WtsLogger.i("DeepSeekExecution: Step 6 標準範本回答生成模組執行")
            val response = try {
                val templateResult = standardTemplateGenerator.process(context)
                if (templateResult.success) {
                    templateResult.data as PersonalizedResponse
                } else {
                    WtsLogger.w("DeepSeekExecution: 標準範本回答生成失敗，回退到原始回答生成器")
                    val fallbackResult = responseGenerator.process(context)
                    if (fallbackResult.success) {
                        fallbackResult.data as PersonalizedResponse
                    } else {
                        createFallbackResponse("回答生成失敗")
                    }
                }
            } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: 標準範本回答生成異常: ${e.message}", e)
                try {
                    val fallbackResult = responseGenerator.process(context)
                    if (fallbackResult.success) {
                        fallbackResult.data as PersonalizedResponse
                    } else {
                        createFallbackResponse("回答生成失敗")
                    }
                } catch (e2: Exception) {
                    WtsLogger.e("DeepSeekExecution: 回退回答生成也失敗: ${e2.message}", e2)
                    createFallbackResponse("回答生成失敗")
                }
            }
            // Validation gate
            val isValid = response.coreInterpretation.isNotBlank() && response.confidence >= 0.5f
            if (!isValid) {
                WtsLogger.w("DeepSeekExecution: 回答驗證未通過，啟用回退回應")
                return createFallbackResponse("回答品質不足")
            }
            WtsLogger.i("DeepSeekExecution: 解籤完成 - 回答長度=${response.coreInterpretation.length}, confidence=${response.confidence}")

            // 輕量互動記錄（無評分，評分由UI後續補寫）
            try {
                val rawQ = context.metadata["rawQuestion"] as? String ?: ""
                InteractionStore.saveInteraction(
                    fortuneId = fortuneId,
                    question = rawQ,
                    aiResponse = response.coreInterpretation,
                    confidence = response.confidence,
                    rating = null,
                    extras = mapOf(
                        "category" to (context.question?.category?.name ?: ""),
                        "intent" to (context.question?.intent?.name ?: ""),
                        "timeRange" to (context.question?.timeRange ?: ""),
                        "tone" to response.tone.name
                    )
                )
                
                // MVP反向提取：記錄用戶問題和籤文內容映射
                val fortuneData = context.metadata["fortuneMeaning"] as? com.example.wtsaskingforsignature.data.EnhancedModels.FortuneMeaning
                val questionData = context.question
                if (fortuneData != null && questionData != null) {
                    ReverseExtractionTool.recordQuestionFortuneMapping(
                        question = rawQ,
                        fortuneContent = fortuneData.content,
                        category = questionData.category
                    )
                }
            } catch (e: Exception) {
                WtsLogger.e("DeepSeekExecution: interaction logging failed: ${e.message}")
            }

            return response

        } catch (e: Exception) {
            WtsLogger.e("DeepSeekExecution: 執行失敗: ${e.message}", e)
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