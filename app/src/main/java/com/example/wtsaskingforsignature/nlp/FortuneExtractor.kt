package com.example.wtsaskingforsignature.nlp

import android.content.Context
import com.google.gson.Gson
import java.io.IOException

/**
 * Fortune Content Extractor
 * 根據Intent和標籤提取對應的籤文段落
 */
class FortuneExtractor(private val context: Context) {
    
    private val gson = Gson()
    
    /**
     * 從籤文內容中提取指定標籤的段落
     */
    fun extractTaggedContent(fortuneContent: String, requiredTags: List<String>): Map<String, String> {
        val extractedContent = mutableMapOf<String, String>()
        
        requiredTags.forEach { tag ->
            val pattern = "\\[$tag\\]([^\\[]*)".toRegex()
            val match = pattern.find(fortuneContent)
            if (match != null) {
                extractedContent[tag] = match.groupValues[1].trim()
            }
        }
        
        return extractedContent
    }
    
    /**
     * 格式化提取的內容為用戶友好的格式
     */
    fun formatExtractedContent(
        extractedContent: Map<String, String>,
        intent: String,
        timeRange: String
    ): FormattedContent {
        
        val intentChinese = getIntentChinese(intent)
        val timeRangeChinese = getTimeRangeChinese(timeRange)
        
        val relevantContent = extractedContent.filter { (tag, _) ->
            isRelevantToIntent(tag, intent)
        }
        
        return FormattedContent(
            intentChinese = intentChinese,
            timeRangeChinese = timeRangeChinese,
            relevantContent = relevantContent,
            formattedText = buildFormattedText(intentChinese, relevantContent)
        )
    }
    
    /**
     * 構建格式化的文本
     */
    private fun buildFormattedText(intentChinese: String, content: Map<String, String>): String {
        val builder = StringBuilder()
        
        builder.appendLine("你現在問的是【$intentChinese】方向的問題，")
        builder.appendLine("籤文顯示—對應內容：")
        builder.appendLine()
        
        content.forEach { (tag, text) ->
            val tagChinese = getTagChinese(tag)
            builder.appendLine("[$tag]$tagChinese：$text")
        }
        
        return builder.toString()
    }
    
    /**
     * 檢查標籤是否與意圖相關
     */
    private fun isRelevantToIntent(tag: String, intent: String): Boolean {
        return when (tag) {
            "A1" -> true // 求籤吉凶總是相關
            "A2" -> true // 解籤詩總是相關
            "A3" -> true // 流年總是相關
            "B" -> intent == "career"
            "C" -> intent == "wealth"
            "D" -> intent == "self"
            "E" -> intent == "family"
            "F" -> intent in listOf("love", "marriage")
            "G" -> intent == "travel"
            "H" -> intent == "reputation"
            "I" -> intent == "health"
            "J" -> intent == "general_fortune"
            "K" -> intent == "general_fortune"
            "L" -> intent == "general_fortune"
            "M" -> intent == "self"
            "N" -> intent == "general_fortune"
            "O" -> intent == "wealth"
            "P" -> intent == "travel"
            else -> false
        }
    }
    
    /**
     * 獲取意圖的中文名稱
     */
    private fun getIntentChinese(intent: String): String {
        return when (intent) {
            "career" -> "事業"
            "wealth" -> "財運"
            "love" -> "愛情"
            "marriage" -> "婚姻"
            "health" -> "健康"
            "family" -> "家庭"
            "self" -> "自身"
            "reputation" -> "名譽"
            "travel" -> "出行"
            "general_fortune" -> "綜合運程"
            else -> "綜合運程"
        }
    }
    
    /**
     * 獲取時間範圍的中文名稱
     */
    private fun getTimeRangeChinese(timeRange: String): String {
        return when (timeRange) {
            "T1" -> "短期（1-3個月）"
            "T2" -> "中期（3-12個月）"
            "T3" -> "長期（1年以上）"
            else -> "中期（3-12個月）"
        }
    }
    
    /**
     * 獲取標籤的中文名稱
     */
    private fun getTagChinese(tag: String): String {
        return when (tag) {
            "A1" -> "求籤吉凶"
            "A2" -> "黃大仙算命解籤詩"
            "A3" -> "流年"
            "B" -> "事業"
            "C" -> "財富"
            "D" -> "自身"
            "E" -> "家庭"
            "F" -> "姻緣"
            "G" -> "移居"
            "H" -> "名譽"
            "I" -> "健康"
            "J" -> "友誼"
            "K" -> "風水"
            "L" -> "遺失"
            "M" -> "自身"
            "N" -> "天時"
            "O" -> "交易"
            "P" -> "出行"
            else -> tag
        }
    }
    
    /**
     * 格式化的內容結果
     */
    data class FormattedContent(
        val intentChinese: String,
        val timeRangeChinese: String,
        val relevantContent: Map<String, String>,
        val formattedText: String
    )
}
