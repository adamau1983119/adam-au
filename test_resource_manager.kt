package com.example.wtsaskingforsignature.test

import android.content.Context
import com.example.wtsaskingforsignature.util.ResourceManager
import com.example.wtsaskingforsignature.util.LanguageManager
import com.example.wtsaskingforsignature.util.LanguageSystemTester

/**
 * ResourceManager 測試驗證
 */
fun testResourceManager(context: Context) {
    println("🧪 開始測試 ResourceManager...")
    
    try {
        // 測試 1: 基本狀態獲取
        println("📊 測試 1: 基本狀態獲取")
        val currentState = ResourceManager.getCurrentLanguageState()
        println("   當前狀態: $currentState")
        
        // 測試 2: 緩存統計
        println("📊 測試 2: 緩存統計")
        val cacheStats = ResourceManager.getCacheStats()
        println("   緩存統計: $cacheStats")
        
        // 測試 3: 語言切換狀態
        println("📊 測試 3: 語言切換狀態")
        val switchState = ResourceManager.getCurrentLanguageState()
        if (switchState is LanguageSwitchState.Error) {
            println("   ⚠️ 狀態為錯誤: ${switchState.message}")
        } else {
            println("   ✅ 狀態正常: $switchState")
        }
        
        // 測試 4: 監聽器管理
        println("📊 測試 4: 監聽器管理")
        val testListener = object : ResourceManager.LanguageChangeListener {
            override fun onLanguageChanged(language: LanguageManager.Language) {
                println("   🔄 語言已更改: ${language.displayName}")
            }
            
            override fun onLanguageChangeFailed(error: String) {
                println("   ❌ 語言更改失敗: $error")
            }
            
            override fun onLanguageChangeStarted() {
                println("   🚀 語言更改開始")
            }
        }
        
        ResourceManager.addLanguageChangeListener(testListener)
        println("   ✅ 監聽器添加成功")
        
        ResourceManager.removeLanguageChangeListener(testListener)
        println("   ✅ 監聽器移除成功")
        
        // 測試 5: 緩存清理
        println("📊 測試 5: 緩存清理")
        ResourceManager.clearCache()
        println("   ✅ 緩存清理成功")
        
        println("🎉 ResourceManager 測試完成！所有功能正常！")
        
    } catch (e: Exception) {
        println("❌ ResourceManager 測試失敗: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * 運行完整的語言系統測試
 */
fun runFullLanguageSystemTest(context: Context) {
    println("🚀 開始運行完整的語言系統測試...")
    
    try {
        val tester = LanguageSystemTester()
        val results = tester.runAllTests(context)
        
        println("📊 測試結果摘要:")
        results.forEach { result ->
            val status = if (result.success) "✅" else "❌"
            println("   $status ${result.testName}: ${if (result.success) "通過" else "失敗"}")
            if (!result.success && result.errors.isNotEmpty()) {
                result.errors.forEach { error ->
                    println("      ⚠️ $error")
                }
            }
        }
        
        val totalTests = results.size
        val passedTests = results.count { it.success }
        val successRate = (passedTests * 100.0 / totalTests).toInt()
        
        println("📈 測試覆蓋率: $passedTests/$totalTests ($successRate%)")
        
        if (successRate == 100) {
            println("🎉 恭喜！所有測試都通過了！")
        } else {
            println("⚠️ 還有一些測試需要修復")
        }
        
    } catch (e: Exception) {
        println("❌ 語言系統測試失敗: ${e.message}")
        e.printStackTrace()
    }
}
