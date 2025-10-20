package com.example.wtsaskingforsignature.test

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ScrollView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.wtsaskingforsignature.util.WtsLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 真正的模組執行測試Activity
 * 在Android Studio中運行以驗證模組是否真正工作
 */
class ModuleTestActivity : AppCompatActivity() {
    
    private lateinit var resultTextView: TextView
    private lateinit var testButton: Button
    private lateinit var logTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 創建UI
        createUI()
        
        // 設置測試按鈕點擊事件
        testButton.setOnClickListener {
            runRealModuleTest()
        }
        
        WtsLogger.i("ModuleTestActivity: 已創建，準備執行真正的模組測試")
    }
    
    private fun createUI() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        
        // 標題
        val titleText = TextView(this).apply {
            text = "真正的模組執行測試"
            textSize = 18f
            setPadding(0, 0, 0, 16)
        }
        
        // 測試按鈕
        testButton = Button(this).apply {
            text = "執行真正的模組測試"
            setPadding(0, 16, 0, 16)
        }
        
        // 結果顯示
        resultTextView = TextView(this).apply {
            text = "點擊按鈕開始測試..."
            textSize = 14f
            setPadding(0, 16, 0, 16)
        }
        
        // 日誌顯示
        logTextView = TextView(this).apply {
            text = "日誌輸出將顯示在這裡..."
            textSize = 12f
            setPadding(0, 16, 0, 0)
        }
        
        // 滾動視圖
        val scrollView = ScrollView(this)
        val scrollLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        scrollLayout.addView(titleText)
        scrollLayout.addView(testButton)
        scrollLayout.addView(resultTextView)
        scrollLayout.addView(logTextView)
        
        scrollView.addView(scrollLayout)
        layout.addView(scrollView)
        
        setContentView(layout)
    }
    
    private fun runRealModuleTest() {
        testButton.isEnabled = false
        testButton.text = "測試執行中..."
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                WtsLogger.i("=== 開始真正的模組執行測試 ===")
                updateLog("開始執行真正的模組測試...")
                
                // 執行真正的測試
                val testResult = withContext(Dispatchers.IO) {
                    executeRealModuleTest()
                }
                
                // 顯示結果
                resultTextView.text = testResult
                updateLog("測試完成！")
                
            } catch (e: Exception) {
                val errorMsg = "測試失敗: ${e.message}\n${e.stackTraceToString()}"
                resultTextView.text = errorMsg
                updateLog("測試失敗: ${e.message}")
                WtsLogger.e("模組測試失敗", e)
            } finally {
                testButton.isEnabled = true
                testButton.text = "重新執行測試"
            }
        }
    }
    
    private suspend fun executeRealModuleTest(): String {
        return try {
            WtsLogger.i("執行真正的模組測試...")
            
            // 測試案例：歐俊𠎀，男，1983-01-19 10:30，香港；籤文：第40籤；問題：2025年10–12月工作運如何？
            val testQuestion = "歐俊𠎀，男，1983-01-19 10:30，香港；籤文：第40籤；問題：2025年10–12月工作運如何？"
            val fortuneId = 40
            
            WtsLogger.i("測試問題: $testQuestion")
            WtsLogger.i("籤文ID: $fortuneId")
            
            // 創建DeepSeekInterpreter實例
            val interpreter = com.example.wtsaskingforsignature.ai.DeepSeekInterpreter()
            
            // 創建測試用戶資料
            val userProfile = com.example.wtsaskingforsignature.data.EnhancedModels.UserProfile(
                name = "歐俊𠎀",
                age = 41, // 1983年生
                gender = com.example.wtsaskingforsignature.data.EnhancedContext.Gender.MALE,
                birthDate = "1983-01-19",
                birthTime = "10:30",
                birthPlace = "香港"
            )
            
            // 創建初始紫微斗數資料
            val ziweiData = com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis(
                mingGong = com.example.wtsaskingforsignature.data.EnhancedModels.StarAnalysis(
                    mainStar = "天機",
                    secondaryStars = listOf("文昌", "文曲"),
                    siHua = "化科"
                ),
                careerPalace = com.example.wtsaskingforsignature.data.EnhancedModels.PalaceAnalysis(
                    mainStar = "太陽",
                    secondaryStars = listOf("天梁", "左輔"),
                    siHua = "化祿",
                    strength = "強旺"
                ),
                liuNian = com.example.wtsaskingforsignature.data.EnhancedModels.LiuNianAnalysis(
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
            
            WtsLogger.i("解籤完成！")
            
            // 構建結果字符串
            buildString {
                appendLine("=== 真正的模組執行測試結果 ===")
                appendLine()
                appendLine("測試問題: $testQuestion")
                appendLine("籤文ID: $fortuneId")
                appendLine()
                appendLine("=== 解籤結果 ===")
                appendLine("核心解讀: ${response.coreInterpretation}")
                appendLine()
                appendLine("時間指引: ${response.timeGuidance}")
                appendLine()
                appendLine("籤文關聯: ${response.fortuneConnection}")
                appendLine()
                appendLine("個人化建議: ${response.personalizedAdvice.joinToString("; ")}")
                appendLine()
                appendLine("語氣: ${response.tone}")
                appendLine("信心度: ${response.confidence}")
                appendLine("專業度: ${response.professionalLevel}")
                appendLine()
                appendLine("=== 驗證結果 ===")
                
                // 驗證結果
                if (response.coreInterpretation.isNotEmpty()) {
                    appendLine("✅ 核心解讀: 有內容 (${response.coreInterpretation.length} 字)")
                } else {
                    appendLine("❌ 核心解讀: 為空")
                }
                
                if (response.fortuneConnection.contains("第${fortuneId}籤")) {
                    appendLine("✅ 籤文關聯: 包含正確籤文ID")
                } else {
                    appendLine("❌ 籤文關聯: 不包含籤文ID")
                }
                
                if (response.personalizedAdvice.isNotEmpty()) {
                    appendLine("✅ 個人化建議: 有內容 (${response.personalizedAdvice.size} 項)")
                } else {
                    appendLine("❌ 個人化建議: 為空")
                }
                
                if (response.confidence > 0.5f) {
                    appendLine("✅ 信心度: 良好 (${response.confidence})")
                } else {
                    appendLine("⚠️ 信心度: 較低 (${response.confidence})")
                }
                
                if (response.professionalLevel > 0.7f) {
                    appendLine("✅ 專業度: 良好 (${response.professionalLevel})")
                } else {
                    appendLine("⚠️ 專業度: 較低 (${response.professionalLevel})")
                }
                
                appendLine()
                appendLine("=== 真正的模組執行測試完成 ===")
            }
            
        } catch (e: Exception) {
            WtsLogger.e("模組測試執行失敗: ${e.message}", e)
            "測試執行失敗: ${e.message}\n\n堆疊追蹤:\n${e.stackTraceToString()}"
        }
    }
    
    private fun updateLog(message: String) {
        runOnUiThread {
            logTextView.text = "${logTextView.text}\n$message"
        }
    }
}
