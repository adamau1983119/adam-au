// DeepSeek API 測試腳本
// 使用方法：在 Android Studio 中運行此腳本，或在應用中添加測試按鈕

import com.example.wtsaskingforsignature.data.ServiceLocator
import com.example.wtsaskingforsignature.data.api.ChatRequest
import kotlinx.coroutines.runBlocking

fun testDeepSeekAPI() {
    runBlocking {
        try {
            println("開始測試 DeepSeek API...")
            
            // 切換到遠端模式
            ServiceLocator.useRemote = true
            
            // 測試聊天 API
            val request = ChatRequest(
                fortuneId = 1,
                question = "測試連接"
            )
            
            println("發送請求：$request")
            val response = ServiceLocator.api.chat(request)
            println("收到回應：$response")
            
            if (response.messages.isNotEmpty()) {
                println("✅ API 測試成功！")
                println("回應內容：${response.messages.first().content}")
            } else {
                println("❌ API 回應為空")
            }
            
        } catch (e: Exception) {
            println("❌ API 測試失敗：${e.message}")
            e.printStackTrace()
        } finally {
            // 恢復本地模式
            ServiceLocator.useRemote = false
        }
    }
}

// 測試抽籤功能
fun testDrawAPI() {
    runBlocking {
        try {
            println("開始測試抽籤 API...")
            
            ServiceLocator.useRemote = true
            
            val response = ServiceLocator.api.draw()
            println("✅ 抽籤成功：${response.id} - ${response.title}")
            
        } catch (e: Exception) {
            println("❌ 抽籤測試失敗：${e.message}")
            e.printStackTrace()
        } finally {
            ServiceLocator.useRemote = false
        }
    }
}

// 測試籤文獲取
fun testFortuneAPI(id: Int) {
    runBlocking {
        try {
            println("開始測試籤文獲取 API...")
            
            ServiceLocator.useRemote = true
            
            val response = ServiceLocator.api.getFortune(id)
            println("✅ 籤文獲取成功：${response.id} - ${response.title}")
            println("內容：${response.content?.take(100)}...")
            
        } catch (e: Exception) {
            println("❌ 籤文獲取測試失敗：${e.message}")
            e.printStackTrace()
        } finally {
            ServiceLocator.useRemote = false
        }
    }
}

// 主測試函數
fun runAllTests() {
    println("=== DeepSeek API 測試套件 ===")
    println()
    
    testDrawAPI()
    println()
    
    testFortuneAPI(1)
    println()
    
    testDeepSeekAPI()
    println()
    
    println("=== 測試完成 ===")
}

// 如果直接運行此腳本
fun main() {
    runAllTests()
}
