package com.example.wtsaskingforsignature.test

import com.example.wtsaskingforsignature.ai.DeepSeekInterpreter
import com.example.wtsaskingforsignature.data.EnhancedContext.Gender
import com.example.wtsaskingforsignature.data.EnhancedModels.PalaceAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.StarAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.UserProfile
import com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.LiuNianAnalysis
import com.example.wtsaskingforsignature.util.WtsLogger
import kotlinx.coroutines.runBlocking

/**
 * 真正的模組執行測試
 * 驗證所有模組是否真正工作
 */
class RealModuleExecutionTest {
    
    fun runRealTest() {
        WtsLogger.i("=== 開始真正的模組執行測試 ===")
        
        runBlocking {
            try {
                // 測試案例：歐俊𠎀，男，1983-01-19 10:30，香港；籤文：第40籤；問題：2025年10–12月工作運如何？
                val testQuestion = "歐俊𠎀，男，1983-01-19 10:30，香港；籤文：第40籤；問題：2025年10–12月工作運如何？"
                val fortuneId = 40
                
                WtsLogger.i("測試問題: $testQuestion")
                WtsLogger.i("籤文ID: $fortuneId")
                
                // 創建DeepSeekInterpreter實例
                val interpreter = DeepSeekInterpreter()
                
                // 創建測試用戶資料
                val userProfile = UserProfile(
                    name = "歐俊𠎀",
                    age = 41, // 1983年生
                    gender = Gender.MALE,
                    birthDate = "1983-01-19",
                    birthTime = "10:30",
                    birthPlace = "香港"
                )
                
                // 創建初始紫微斗數資料
                val ziweiData = ZiweiAnalysis(
                    mingGong = StarAnalysis(
                        mainStar = "天機",
                        secondaryStars = listOf("文昌", "文曲"),
                        siHua = "化科"
                    ),
                    careerPalace = PalaceAnalysis(
                        mainStar = "太陽",
                        secondaryStars = listOf("天梁", "左輔"),
                        siHua = "化祿",
                        strength = "強旺"
                    ),
                    liuNian = LiuNianAnalysis(
                        year = 2025,
                        careerTrend = "上升",
                        keyMonths = listOf("10月", "11月", "12月"),
                        opportunities = "貴人相助、資源對接"
                    )
                )
                
                WtsLogger.i("開始執行DeepSeekInterpreter...")
                
                // 真正執行解籤
                val response = interpreter.interpretFortune(
                    question = testQuestion,
                    userProfile = userProfile,
                    ziweiData = ziweiData,
                    fortuneId = fortuneId
                )
                
                WtsLogger.i("=== 解籤結果 ===")
                WtsLogger.i("核心解讀: ${response.coreInterpretation}")
                WtsLogger.i("時間指引: ${response.timeGuidance}")
                WtsLogger.i("籤文關聯: ${response.fortuneConnection}")
                WtsLogger.i("個人化建議: ${response.personalizedAdvice.joinToString("; ")}")
                WtsLogger.i("語氣: ${response.tone}")
                WtsLogger.i("信心度: ${response.confidence}")
                WtsLogger.i("專業度: ${response.professionalLevel}")
                
                // 驗證結果
                validateResponse(response, fortuneId)
                
                WtsLogger.i("=== 真正的模組執行測試完成 ===")
                
            } catch (e: Exception) {
                WtsLogger.e("測試執行失敗: ${e.message}", e)
                WtsLogger.e("堆疊追蹤: ${e.stackTraceToString()}")
            }
        }
    }
    
    private fun validateResponse(response: com.example.wtsaskingforsignature.data.EnhancedContext.PersonalizedResponse, fortuneId: Int) {
        WtsLogger.i("=== 驗證解籤結果 ===")
        
        // 驗證核心解讀不為空
        if (response.coreInterpretation.isNotEmpty()) {
            WtsLogger.i("✅ 核心解讀: 有內容 (${response.coreInterpretation.length} 字)")
        } else {
            WtsLogger.e("❌ 核心解讀: 為空")
        }
        
        // 驗證籤文關聯包含籤文ID
        if (response.fortuneConnection.contains("第${fortuneId}籤")) {
            WtsLogger.i("✅ 籤文關聯: 包含正確籤文ID")
        } else {
            WtsLogger.e("❌ 籤文關聯: 不包含籤文ID")
        }
        
        // 驗證個人化建議不為空
        if (response.personalizedAdvice.isNotEmpty()) {
            WtsLogger.i("✅ 個人化建議: 有內容 (${response.personalizedAdvice.size} 項)")
        } else {
            WtsLogger.e("❌ 個人化建議: 為空")
        }
        
        // 驗證信心度
        if (response.confidence > 0.5f) {
            WtsLogger.i("✅ 信心度: 良好 (${response.confidence})")
        } else {
            WtsLogger.w("⚠️ 信心度: 較低 (${response.confidence})")
        }
        
        // 驗證專業度
        if (response.professionalLevel > 0.7f) {
            WtsLogger.i("✅ 專業度: 良好 (${response.professionalLevel})")
        } else {
            WtsLogger.w("⚠️ 專業度: 較低 (${response.professionalLevel})")
        }
    }
}
