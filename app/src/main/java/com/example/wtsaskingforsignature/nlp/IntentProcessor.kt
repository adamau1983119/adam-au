package com.example.wtsaskingforsignature.nlp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * Intent Processing Module for DeepSeek Fortune Interpretation
 * 處理用戶問題的意圖識別和籤文段落對應
 */
class IntentProcessor(private val context: Context) {
    
    private val gson = Gson()
    private var intentMapping: Map<String, Any>? = null
    
    init {
        loadIntentMapping()
    }
    
    /**
     * 載入Intent對應表
     */
    private fun loadIntentMapping() {
        try {
            val json = context.assets.open("intent_mapping.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, Any>>() {}.type
            intentMapping = gson.fromJson(json, type)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    /**
     * 分析用戶問題，識別意圖和時間範圍
     */
    fun analyzeUserQuestion(question: String): QuestionAnalysis {
        val intent = identifyIntent(question)
        val timeRange = identifyTimeRange(question)
        val requiredTags = getRequiredTags(intent)
        
        return QuestionAnalysis(
            originalQuestion = question,
            intent = intent,
            timeRange = timeRange,
            requiredTags = requiredTags,
            confidence = calculateConfidence(question, intent)
        )
    }
    
    /**
     * 識別問題意圖
     */
    private fun identifyIntent(question: String): String {
        val keywords = mapOf(
            "career" to listOf("事業", "工作", "升遷", "職場", "公司", "老闆", "同事"),
            "wealth" to listOf("財運", "金錢", "投資", "理財", "賺錢", "破財", "橫財"),
            "love" to listOf("愛情", "戀愛", "感情", "男朋友", "女朋友", "追求", "分手"),
            "marriage" to listOf("婚姻", "結婚", "夫妻", "老公", "老婆", "離婚", "婚禮"),
            "health" to listOf("健康", "身體", "疾病", "生病", "醫院", "醫生", "治療"),
            "family" to listOf("家庭", "家人", "父母", "子女", "孩子", "親戚", "家事"),
            "self" to listOf("自身", "個人", "性格", "發展", "成長", "改變", "自己"),
            "reputation" to listOf("名譽", "學業", "考試", "讀書", "學校", "老師", "成績"),
            "travel" to listOf("出行", "旅行", "旅遊", "搬遷", "移民", "搬家", "出國"),
            "general_fortune" to listOf("運勢", "運程", "整體", "綜合", "未來", "前途", "命運")
        )
        
        var maxScore = 0
        var bestIntent = "general_fortune"
        
        keywords.forEach { (intent, words) ->
            val score = words.count { question.contains(it) }
            if (score > maxScore) {
                maxScore = score
                bestIntent = intent
            }
        }
        
        return bestIntent
    }
    
    /**
     * 識別時間範圍
     */
    private fun identifyTimeRange(question: String): String {
        val timeKeywords = mapOf(
            "T1" to listOf("近期", "短期", "這個月", "下個月", "最近", "短期內"),
            "T2" to listOf("今年", "半年", "中期", "年內", "這一年", "今年內"),
            "T3" to listOf("長期", "未來", "長遠", "幾年", "將來", "未來幾年")
        )
        
        timeKeywords.forEach { (timeRange, keywords) ->
            if (keywords.any { question.contains(it) }) {
                return timeRange
            }
        }
        
        return "T2" // 默認為中期
    }
    
    /**
     * 獲取對應的籤文標籤
     */
    private fun getRequiredTags(intent: String): List<String> {
        val mapping = intentMapping?.get("intent_categories") as? Map<String, Any>
        val intentData = mapping?.get(intent) as? Map<String, Any>
        return intentData?.get("required_tags") as? List<String> ?: listOf("A1")
    }
    
    /**
     * 計算識別信心度
     */
    private fun calculateConfidence(question: String, intent: String): Float {
        // 簡單的信心度計算，可根據需要優化
        val keywords = when (intent) {
            "career" -> listOf("事業", "工作", "升遷", "職場")
            "wealth" -> listOf("財運", "金錢", "投資", "理財")
            "love" -> listOf("愛情", "戀愛", "感情")
            "marriage" -> listOf("婚姻", "結婚", "夫妻")
            "health" -> listOf("健康", "身體", "疾病")
            "family" -> listOf("家庭", "家人", "子女")
            "self" -> listOf("自身", "個人", "性格")
            "reputation" -> listOf("名譽", "學業", "考試")
            "travel" -> listOf("出行", "旅行", "搬遷")
            else -> listOf("運勢", "運程", "整體")
        }
        
        val matchCount = keywords.count { question.contains(it) }
        return (matchCount.toFloat() / keywords.size).coerceAtMost(1.0f)
    }
    
    /**
     * 問題分析結果
     */
    data class QuestionAnalysis(
        val originalQuestion: String,
        val intent: String,
        val timeRange: String,
        val requiredTags: List<String>,
        val confidence: Float
    )
}
