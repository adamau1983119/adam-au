package com.example.wtsaskingforsignature

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionAnalysis
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionIntent
import com.example.wtsaskingforsignature.data.EnhancedModels.FortuneMeaning
import com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.PalaceAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.StarAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.UserProfile
import com.example.wtsaskingforsignature.data.EnhancedModels.Gender
import com.example.wtsaskingforsignature.modules.StandardTemplateGenerator
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

/**
 * 標準範本生成器測試
 * 驗證4步驟格式輸出效果
 */
class StandardTemplateTest {
    
    @Test
    fun testStandardTemplateOutput() = runBlocking {
        // 準備測試數據
        val userProfile = UserProfile(
            name = "歐俊傑",
            birthDate = "1983-01-19",
            birthTime = "10:30",
            birthPlace = "香港",
            gender = Gender.MALE,
            age = 41
        )
        
        val questionAnalysis = QuestionAnalysis(
            category = QuestionCategory.CAREER,
            intent = QuestionIntent.PREDICTION,
            timeRange = "2025年11月-12月",
            emotion = com.example.wtsaskingforsignature.data.EnhancedContext.SentimentScore.NEUTRAL,
            urgency = com.example.wtsaskingforsignature.data.EnhancedContext.UrgencyLevel.MEDIUM
        )
        
        val fortuneMeaning = FortuneMeaning(
            id = 1,
            title = "姜公封相",
            summary = "求籤吉凶：上上靈籤",
            content = """算命籤詩：
靈籤求得第一枝 龍虎風雲際會時
一旦凌霄揚自樂 任君來往赴瑤池
黃大仙算命解籤詩：
算命求得這支上上籤詩，可喜可賀！龍虎得風雲際會，您立下的大志，將會得償所願，並能到達至善至樂的境界。正所謂東成西就，左右逢源，萬事如意。轉載自算命眼
黃大仙算命解運勢：
功名遂，求財豐，六畜吉，家宅隆，病即愈，蚕有功
孕生子，婚姻同，行人至，快如風，若謀望，盡享通""",
            meaning = "事業有成",
            symbol = "貴人扶持"
        )
        
        val ziweiAnalysis = ZiweiAnalysis(
            mingGong = StarAnalysis(
                mainStar = "天同",
                assistantStars = listOf("文昌", "文曲"),
                siHua = "化祿",
                strength = "旺"
            ),
            careerPalace = PalaceAnalysis(
                mainStar = "天府",
                assistantStars = listOf("祿存", "文昌"),
                siHua = "化祿",
                strength = "旺"
            ),
            lovePalace = PalaceAnalysis(
                mainStar = "天相",
                assistantStars = listOf("化科"),
                siHua = "化科",
                strength = "平"
            ),
            wealthPalace = PalaceAnalysis(
                mainStar = "武曲",
                assistantStars = listOf("化祿"),
                siHua = "化祿",
                strength = "旺"
            ),
            healthPalace = PalaceAnalysis(
                mainStar = "天梁",
                assistantStars = listOf("化科"),
                siHua = "化科",
                strength = "平"
            )
        )
        
        val context = AnalysisContext(
            question = questionAnalysis,
            ziweiData = ziweiAnalysis,
            fortuneId = 1,
            userProfile = userProfile,
            metadata = mutableMapOf("fortuneMeaning" to fortuneMeaning)
        )
        
        // 執行測試
        val generator = StandardTemplateGenerator()
        val result = generator.process(context)
        
        // 驗證結果
        assertTrue("標準範本生成應該成功", result.success)
        assertNotNull("回答數據不應為空", result.data)
        
        val response = result.data as com.example.wtsaskingforsignature.data.EnhancedContext.PersonalizedResponse
        val output = response.coreInterpretation
        
        // 驗證Step 1：靈籤基本資訊
        assertTrue("應包含靈籤基本資訊", output.contains("🔮 Step 1：靈籤基本資訊"))
        assertTrue("應包含黃大仙靈籤", output.contains("黃大仙靈籤是：黃大仙靈籤01"))
        assertTrue("應包含籤文標題", output.contains("第01靈籤：姜公封相"))
        assertTrue("應包含籤詩內容", output.contains("靈籤求得第一枝"))
        
        // 驗證Step 2：問題方向 + 籤文內容
        assertTrue("應包含問題方向分析", output.contains("🎯 Step 2：問題方向 + 對應籤文內容"))
        assertTrue("應包含事業方向", output.contains("你現在問的是事業方向的問題"))
        assertTrue("應包含籤文解釋", output.contains("籤文顯示—事業方向"))
        
        // 驗證Step 3：紫微斗數盤位分析
        assertTrue("應包含紫微斗數分析", output.contains("🧭 Step 3：紫微斗數盤位分析"))
        assertTrue("應包含主星分析", output.contains("主星為：天府"))
        assertTrue("應包含宮位分析", output.contains("位於官祿宮"))
        assertTrue("應包含流年運勢", output.contains("2025年11月-12月期間"))
        
        // 驗證Step 4：AI整合解籤內容
        assertTrue("應包含AI整合分析", output.contains("🤖 Step 4：AI整合解籤內容"))
        assertTrue("應包含綜合分析", output.contains("綜合靈籤內容與紫微斗數命盤分析"))
        assertTrue("應包含建議", output.contains("建議你在此期間"))
        
        println("=== 標準範本輸出測試結果 ===")
        println(output)
        println("=== 測試完成 ===")
    }
    
    @Test
    fun testLoveCategoryTemplate() = runBlocking {
        // 測試愛情類別
        val questionAnalysis = QuestionAnalysis(
            category = QuestionCategory.LOVE,
            intent = QuestionIntent.PREDICTION,
            timeRange = "2025年11月-12月"
        )
        
        val fortuneMeaning = FortuneMeaning(
            id = 38,
            title = "第38籤",
            summary = "求籤吉凶：中平靈籤",
            content = """算命籤詩：
婚姻同，行人至，快如風，若謀望，盡享通""",
            meaning = "感情發展",
            symbol = "桃花運"
        )
        
        val context = AnalysisContext(
            question = questionAnalysis,
            fortuneId = 38,
            metadata = mutableMapOf("fortuneMeaning" to fortuneMeaning)
        )
        
        val generator = StandardTemplateGenerator()
        val result = generator.process(context)
        
        assertTrue("愛情類別測試應該成功", result.success)
        val response = result.data as com.example.wtsaskingforsignature.data.EnhancedContext.PersonalizedResponse
        val output = response.coreInterpretation
        
        assertTrue("應包含愛情方向", output.contains("你現在問的是愛情感情的問題"))
        assertTrue("應包含夫妻宮分析", output.contains("夫妻宮"))
        
        println("=== 愛情類別測試結果 ===")
        println(output)
    }
}
