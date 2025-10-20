// 測試腳本 - 歐俊𠎀第40籤案例
package com.example.wtsaskingforsignature.test

import com.example.wtsaskingforsignature.ai.DeepSeekInterpreter
import com.example.wtsaskingforsignature.data.EnhancedModels.*
import com.example.wtsaskingforsignature.data.EnhancedContext.*

fun main() {
    println("=== 測試歐俊𠎀第40籤案例 ===")
    
    // 創建測試資料
    val userProfile = UserProfile(
        name = "歐俊𠎀",
        age = 41, // 1983年生
        gender = Gender.MALE,
        birthDate = "1983-01-19",
        birthTime = "10:30",
        birthPlace = "香港"
    )
    
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
    
    val question = "2025年10–12月工作運如何？"
    val fortuneId = 40
    
    // 創建解釋器並生成回答
    val interpreter = DeepSeekInterpreter()
    
    try {
        val response = interpreter.interpretFortune(
            question = question,
            userProfile = userProfile,
            ziweiData = ziweiData,
            fortuneId = fortuneId
        )
        
        println("=== 生成的回答 ===")
        println("核心解讀：${response.coreInterpretation}")
        println("\n時間指引：${response.timeGuidance}")
        println("\n籤文寓意：${response.fortuneConnection}")
        println("\n個人化建議：${response.personalizedAdvice.joinToString("；")}")
        println("\n語氣風格：${response.tone}")
        println("\n信心度：${response.confidence}")
        println("\n專業程度：${response.professionalLevel}")
        
    } catch (e: Exception) {
        println("測試失敗：${e.message}")
        e.printStackTrace()
    }
}
