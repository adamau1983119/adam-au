package com.example.wtsaskingforsignature.ai

import android.content.Context
import android.content.res.AssetManager
import com.example.wtsaskingforsignature.data.api.ChatMessage
import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.util.WtsLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 本地 AI 解籤服務
 * 使用預訓練的規則和模板進行智能解籤
 */
class LocalAIService(private val context: Context) {
    
    private val fortuneTemplates = mapOf(
        "事業" to listOf(
            "此籤顯示事業方面{aspect}，建議{advice}",
            "事業運勢{trend}，關鍵在於{key_point}",
            "當前階段宜{action}，避免{avoid}"
        ),
        "財運" to listOf(
            "財運{trend}，{timing}是關鍵時機",
            "理財建議：{advice}，注意{caution}",
            "投資方向：{direction}，風險{risk_level}"
        ),
        "感情" to listOf(
            "感情運勢{trend}，{timing}適合{action}",
            "關係發展：{development}，建議{advice}",
            "桃花運{level}，{location}是幸運地點"
        ),
        "健康" to listOf(
            "健康狀況{status}，{organ}需要特別注意",
            "養生建議：{advice}，避免{avoid}",
            "運動建議：{exercise}，飲食{nutrition}"
        )
    )
    
    private val fortuneKeywords = mapOf(
        "上上籤" to mapOf(
            "trend" to "極佳",
            "advice" to "積極進取，把握機會",
            "timing" to "現在",
            "action" to "大膽行動"
        ),
        "上籤" to mapOf(
            "trend" to "良好",
            "advice" to "穩步前進，保持信心",
            "timing" to "近期",
            "action" to "適度擴展"
        ),
        "中籤" to mapOf(
            "trend" to "平穩",
            "advice" to "謹慎行事，穩中求進",
            "timing" to "耐心等待",
            "action" to "維持現狀"
        ),
        "下籤" to mapOf(
            "trend" to "需改善",
            "advice" to "調整心態，避免衝動",
            "timing" to "暫時觀望",
            "action" to "自我反省"
        )
    )
    
    /**
     * 本地 AI 解籤
     */
    suspend fun interpretFortune(
        fortuneId: Int,
        fortuneContent: String,
        question: String,
        category: String? = null
    ): ChatResponse = withContext(Dispatchers.IO) {
        
        try {
            WtsLogger.i("本地 AI 開始解籤：籤文 $fortuneId")
            
            // 分析籤文內容
            val analysis = analyzeFortuneContent(fortuneContent)
            
            // 根據問題類型生成回應
            val response = generateResponse(question, analysis, category)
            
            // 創建 AI 回應
            val aiMessage = ChatMessage(
                role = "assistant",
                content = response
            )
            
            WtsLogger.i("本地 AI 解籤完成")
            
            ChatResponse(messages = listOf(aiMessage))
            
        } catch (e: Exception) {
            WtsLogger.e("本地 AI 解籤失敗：${e.message}")
            
            // 返回預設回應
            val fallbackMessage = ChatMessage(
                role = "assistant",
                content = "抱歉，解籤服務暫時不可用。請稍後再試或聯繫客服。"
            )
            
            ChatResponse(messages = listOf(fallbackMessage))
        }
    }
    
    /**
     * 分析籤文內容
     */
    private fun analyzeFortuneContent(content: String): Map<String, String> {
        val analysis = mutableMapOf<String, String>()
        
        // 分析籤文等級
        when {
            content.contains("上上籤") -> analysis["level"] = "上上籤"
            content.contains("上籤") -> analysis["level"] = "上籤"
            content.contains("中籤") -> analysis["level"] = "中籤"
            content.contains("下籤") -> analysis["level"] = "下籤"
            else -> analysis["level"] = "中籤"
        }
        
        // 分析籤文主題
        when {
            content.contains("事業") || content.contains("工作") -> analysis["theme"] = "事業"
            content.contains("財運") || content.contains("金錢") -> analysis["theme"] = "財運"
            content.contains("感情") || content.contains("婚姻") -> analysis["theme"] = "感情"
            content.contains("健康") || content.contains("身體") -> analysis["theme"] = "健康"
            else -> analysis["theme"] = "綜合"
        }
        
        // 分析籤文含義
        analysis["meaning"] = extractKeyMeaning(content)
        
        return analysis
    }
    
    /**
     * 提取籤文關鍵含義
     */
    private fun extractKeyMeaning(content: String): String {
        val lines = content.lines()
        val keyLines = lines.filter { line ->
            line.contains("籤詩") || line.contains("解籤") || line.contains("釋義")
        }
        
        return if (keyLines.isNotEmpty()) {
            keyLines.first().replace(Regex(".*[:：]"), "").trim()
        } else {
            content.lines().firstOrNull()?.trim() ?: "籤文含義需細心體會"
        }
    }
    
    /**
     * 生成 AI 回應
     */
    private fun generateResponse(
        question: String,
        analysis: Map<String, String>,
        category: String?
    ): String {
        val level = analysis["level"] ?: "中籤"
        val theme = analysis["theme"] ?: "綜合"
        val meaning = analysis["meaning"] ?: ""
        
        val keywords = fortuneKeywords[level] ?: emptyMap()
        
        // 根據問題類型生成回應
        val response = when {
            question.contains("事業") || question.contains("工作") -> {
                generateCareerAdvice(level, keywords, meaning)
            }
            question.contains("財運") || question.contains("金錢") -> {
                generateWealthAdvice(level, keywords, meaning)
            }
            question.contains("感情") || question.contains("婚姻") -> {
                generateLoveAdvice(level, keywords, meaning)
            }
            question.contains("健康") || question.contains("身體") -> {
                generateHealthAdvice(level, keywords, meaning)
            }
            else -> {
                generateGeneralAdvice(level, keywords, meaning, question)
            }
        }
        
        return buildString {
            appendLine("【AI 智能解籤】")
            appendLine("籤文等級：$level")
            appendLine("籤文主題：$theme")
            appendLine()
            appendLine("【解籤分析】")
            appendLine(meaning)
            appendLine()
            appendLine("【智能建議】")
            appendLine(response)
            appendLine()
            appendLine("【溫馨提醒】")
            appendLine("此解籤僅供參考，具體行動請結合實際情況。保持積極心態，相信美好未來！")
        }
    }
    
    private fun generateCareerAdvice(level: String, keywords: Map<String, String>, meaning: String): String {
        when (level) {
            "上上籤" -> return "事業運勢極佳，適合大膽進取。建議：1) 把握當下機會 2) 展現領導才能 3) 擴展業務範圍 4) 建立人脈網絡 5) 投資自我提升"
            "上籤" -> return "事業發展良好，穩步上升。建議：1) 保持專業水準 2) 加強團隊合作 3) 學習新技能 4) 規劃長期目標 5) 維護良好聲譽"
            "中籤" -> return "事業運勢平穩，需要耐心。建議：1) 專注當前工作 2) 提升工作效率 3) 改善溝通技巧 4) 尋求導師指導 5) 保持學習態度"
            else -> return "事業面臨挑戰，需要調整。建議：1) 檢視工作方法 2) 改善人際關係 3) 提升專業能力 4) 尋求新的機會 5) 保持積極心態"
        }
    }
    
    private fun generateWealthAdvice(level: String, keywords: Map<String, String>, meaning: String): String {
        when (level) {
            "上上籤" -> return "財運極佳，投資機會多。建議：1) 把握投資時機 2) 分散投資組合 3) 學習理財知識 4) 建立被動收入 5) 謹慎風險控制"
            "上籤" -> return "財運良好，收入穩定增長。建議：1) 制定理財計劃 2) 適度投資理財 3) 控制不必要支出 4) 建立應急基金 5) 尋求理財建議"
            "中籤" -> return "財運平穩，收支平衡。建議：1) 量入為出 2) 避免衝動消費 3) 學習節儉理財 4) 尋找額外收入 5) 規劃未來支出"
            else -> return "財運需改善，需要調整。建議：1) 檢視支出習慣 2) 制定預算計劃 3) 避免高風險投資 4) 尋求理財顧問 5) 培養儲蓄習慣"
        }
    }
    
    private fun generateLoveAdvice(level: String, keywords: Map<String, String>, meaning: String): String {
        when (level) {
            "上上籤" -> return "感情運勢極佳，桃花旺盛。建議：1) 主動表達愛意 2) 參加社交活動 3) 展現個人魅力 4) 珍惜眼前人 5) 規劃美好未來"
            "上籤" -> return "感情發展良好，關係穩定。建議：1) 增進彼此了解 2) 創造浪漫時刻 3) 共同成長進步 4) 解決小摩擦 5) 規劃共同目標"
            "中籤" -> return "感情運勢平穩，需要耐心。建議：1) 保持真誠溝通 2) 理解對方感受 3) 尋找共同興趣 4) 給彼此空間 5) 培養信任關係"
            else -> return "感情面臨挑戰，需要努力。建議：1) 檢視關係問題 2) 改善溝通方式 3) 尋求專業建議 4) 給彼此時間 5) 保持希望信心"
        }
    }
    
    private fun generateHealthAdvice(level: String, keywords: Map<String, String>, meaning: String): String {
        when (level) {
            "上上籤" -> return "健康狀況極佳，精力充沛。建議：1) 保持運動習慣 2) 均衡飲食營養 3) 充足睡眠休息 4) 定期健康檢查 5) 分享健康生活"
            "上籤" -> return "健康狀況良好，身體強健。建議：1) 規律運動鍛煉 2) 注意飲食衛生 3) 保持良好作息 4) 預防保健措施 5) 培養健康興趣"
            "中籤" -> return "健康狀況平穩，需要關注。建議：1) 適度運動鍛煉 2) 調整飲食習慣 3) 改善睡眠質量 4) 定期體檢 5) 學習健康知識"
            else -> return "健康需要改善，需要調養。建議：1) 諮詢醫生建議 2) 調整生活方式 3) 避免不良習慣 4) 保持樂觀心態 5) 尋求專業幫助"
        }
    }
    
    private fun generateGeneralAdvice(level: String, keywords: Map<String, String>, meaning: String, question: String): String {
        when (level) {
            "上上籤" -> return "整體運勢極佳，諸事順遂。建議：1) 把握當下機會 2) 積極進取行動 3) 幫助他人成長 4) 規劃長遠目標 5) 保持謙虛態度"
            "上籤" -> return "整體運勢良好，穩步發展。建議：1) 保持積極心態 2) 專注重要事務 3) 維護人際關係 4) 學習新知識 5) 感恩當下擁有"
            "中籤" -> return "整體運勢平穩，需要努力。建議：1) 保持耐心毅力 2) 專注當前目標 3) 改善不足之處 4) 尋求他人幫助 5) 相信美好未來"
            else -> return "整體運勢需改善，需要調整。建議：1) 檢視生活方向 2) 調整心態觀念 3) 尋求專業指導 4) 培養積極習慣 5) 保持希望信心"
        }
    }
}
