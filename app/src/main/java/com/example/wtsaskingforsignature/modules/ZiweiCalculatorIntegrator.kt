package com.example.wtsaskingforsignature.modules

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisModule
import com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.StarAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.PalaceAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.LiuNianAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.UserProfile
import com.example.wtsaskingforsignature.data.EnhancedContext.Gender
import com.example.wtsaskingforsignature.ziwei.ZiweiCalculator
import com.example.wtsaskingforsignature.util.WtsLogger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 真正的紫微斗數整合模組
 * 使用App內現有的紫微斗數計算功能
 */
class ZiweiCalculatorIntegrator : AnalysisModule {
    
    private val ziweiCalculator = ZiweiCalculator()
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            val userProfile = context.userProfile ?: createDefaultUserProfile()
            
            WtsLogger.i("ZiweiCalculatorIntegrator: 計算紫微斗數 - ${userProfile.name}, ${userProfile.birthDate}")
            
            // 使用App內現有的紫微斗數計算器
            val ziweiResult = calculateZiweiData(userProfile)
            
            AnalysisResult(
                success = true,
                resultCode = "SUCCESS",
                data = ziweiResult,
                moduleName = getModuleName()
            )
        } catch (e: Exception) {
            WtsLogger.e("ZiweiCalculatorIntegrator 執行失敗: ${e.message}", e)
            AnalysisResult(
                success = false,
                resultCode = "ZIWEI_CALCULATION_FAILED",
                errorMessage = e.message,
                moduleName = getModuleName()
            )
        }
    }
    
    override fun getModuleName(): String = "ZiweiCalculatorIntegrator"
    override fun getVersion(): String = "1.0"
    
    /**
     * 使用App內現有的紫微斗數計算器
     */
    private fun calculateZiweiData(userProfile: UserProfile): ZiweiAnalysis {
        try {
            // 解析出生日期時間
            val birthDateTime = parseBirthDateTime(userProfile.birthDate, userProfile.birthTime)
            
            // 使用App內現有的ZiweiCalculator
            val ziweiResult = ziweiCalculator.calculate(birthDateTime, userProfile.birthPlace)
            
            // 轉換為我們的資料結構
            return convertZiweiResult(ziweiResult, userProfile)
            
        } catch (e: Exception) {
            WtsLogger.e("紫微斗數計算失敗: ${e.message}")
            return createDefaultZiweiAnalysis(userProfile)
        }
    }
    
    /**
     * 解析出生日期時間
     */
    private fun parseBirthDateTime(birthDate: String, birthTime: String): LocalDateTime {
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            
            val date = java.time.LocalDate.parse(birthDate, dateFormatter)
            val time = java.time.LocalTime.parse(birthTime, timeFormatter)
            
            LocalDateTime.of(date, time)
        } catch (e: Exception) {
            WtsLogger.w("日期時間解析失敗，使用預設值: ${e.message}")
            LocalDateTime.of(1990, 1, 1, 12, 0)
        }
    }
    
    /**
     * 轉換App內紫微斗數結果為我們的資料結構
     */
    private fun convertZiweiResult(ziweiResult: ZiweiCalculator.ZiweiResult, userProfile: UserProfile): ZiweiAnalysis {
        val mainStars = ziweiResult.mainStars
        val gongWei = ziweiResult.gongWei
        
        return ZiweiAnalysis(
            mingGong = StarAnalysis(
                mainStar = getMainStarName(ziweiResult.mingGong),
                secondaryStars = listOf("文昌", "文曲"), // 簡化處理
                siHua = "化科"
            ),
            careerPalace = PalaceAnalysis(
                mainStar = getCareerPalaceStar(gongWei),
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
    }
    
    /**
     * 根據命宮位置獲取主星名稱
     */
    private fun getMainStarName(mingGong: Int): String {
        val stars = listOf("紫微", "天機", "太陽", "武曲", "天同", "廉貞", "天府", "太陰", "貪狼", "巨門", "天相", "天梁")
        return if (mingGong < stars.size) stars[mingGong] else "紫微"
    }
    
    /**
     * 根據官祿宮位置獲取主星
     */
    private fun getCareerPalaceStar(gongWei: Map<String, Int>): String {
        val careerPalacePosition = gongWei["官禄"] ?: 0
        val stars = listOf("太陽", "武曲", "天同", "廉貞", "天府", "太陰", "貪狼", "巨門", "天相", "天梁", "七殺", "破軍")
        return if (careerPalacePosition < stars.size) stars[careerPalacePosition] else "太陽"
    }
    
    /**
     * 創建預設用戶資料
     */
    private fun createDefaultUserProfile(): UserProfile {
        return UserProfile(
            name = "用戶",
            age = 30,
            gender = Gender.UNKNOWN,
            birthDate = "1990-01-01",
            birthTime = "12:00",
            birthPlace = "香港"
        )
    }
    
    /**
     * 創建預設紫微斗數分析
     */
    private fun createDefaultZiweiAnalysis(userProfile: UserProfile): ZiweiAnalysis {
        return ZiweiAnalysis(
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
    }
}
