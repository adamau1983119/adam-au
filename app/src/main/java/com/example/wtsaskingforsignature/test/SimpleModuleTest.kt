package com.example.wtsaskingforsignature.test

import com.example.wtsaskingforsignature.data.ServiceLocator
import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.util.WtsLogger
import kotlinx.coroutines.runBlocking

/**
 * 簡單的模組調用測試
 * 驗證現有App是否真的會調用新模組
 */
class SimpleModuleTest {
    
    fun testModuleCall(): String {
        return runBlocking {
            try {
                WtsLogger.i("=== 開始簡單模組調用測試 ===")
                
                // 測試案例
                val fortuneId = 40
                val question = "歐俊𠎀，男，1983-01-19 10:30，香港；籤文：第40籤；問題：2025年10–12月工作運如何？"
                
                WtsLogger.i("測試參數: fortuneId=$fortuneId, question=$question")
                
                // 調用現有App的chat方法
                val result = ServiceLocator.repository.chat(fortuneId, question)
                
                if (result.isSuccess) {
                    val response = result.getOrThrow()
                    WtsLogger.i("調用成功: ${response.messages.size} 條消息")
                    
                    val assistantMessage = response.messages.find { it.role.contains("assistant") }
                    if (assistantMessage != null) {
                        WtsLogger.i("AI回答: ${assistantMessage.content}")
                        
                        // 檢查回答是否包含新模組的特徵
                        val hasNewModuleFeatures = checkNewModuleFeatures(assistantMessage.content)
                        
                        return@runBlocking buildString {
                            appendLine("=== 模組調用測試結果 ===")
                            appendLine("✅ 調用成功")
                            appendLine("✅ 收到回答: ${assistantMessage.content.length} 字")
                            appendLine()
                            appendLine("=== 新模組特徵檢查 ===")
                            appendLine("包含籤文ID: ${hasNewModuleFeatures.containsFortuneId}")
                            appendLine("包含時間分析: ${hasNewModuleFeatures.containsTimeAnalysis}")
                            appendLine("包含專業術語: ${hasNewModuleFeatures.containsProfessionalTerms}")
                            appendLine("回答長度適中: ${hasNewModuleFeatures.hasReasonableLength}")
                            appendLine()
                            appendLine("=== 回答內容 ===")
                            appendLine(assistantMessage.content)
                        }
                    } else {
                        return@runBlocking "❌ 沒有收到AI回答"
                    }
                } else {
                    val error = result.exceptionOrNull()
                    WtsLogger.e("調用失敗: ${error?.message}")
                    return@runBlocking "❌ 調用失敗: ${error?.message}"
                }
                
            } catch (e: Exception) {
                WtsLogger.e("測試執行失敗: ${e.message}", e)
                return@runBlocking "❌ 測試執行失敗: ${e.message}\n${e.stackTraceToString()}"
            }
        }
    }
    
    private fun checkNewModuleFeatures(content: String): NewModuleFeatures {
        return NewModuleFeatures(
            containsFortuneId = content.contains("第40籤") || content.contains("伯才碎琴"),
            containsTimeAnalysis = content.contains("10月") || content.contains("11月") || content.contains("12月"),
            containsProfessionalTerms = content.contains("紫微") || content.contains("命宮") || content.contains("化祿"),
            hasReasonableLength = content.length in 100..500
        )
    }
    
    private data class NewModuleFeatures(
        val containsFortuneId: Boolean,
        val containsTimeAnalysis: Boolean,
        val containsProfessionalTerms: Boolean,
        val hasReasonableLength: Boolean
    )
}
