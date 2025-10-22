package com.example.wtsaskingforsignature.utils

import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.config.MVPSemanticConfig
import com.example.wtsaskingforsignature.util.WtsLogger
import org.json.JSONObject
import org.json.JSONArray
import java.io.File
import java.io.FileWriter

/**
 * 反向提取工具
 * 收集用戶問題和靈籤內容，建立「問題語句 → 主題方向」映射表
 */
object ReverseExtractionTool {
    
    private const val EXTRACTION_FILE = "question_fortune_mapping.json"
    private val extractionData = mutableMapOf<String, MutableList<String>>()
    
    /**
     * 記錄用戶問題和對應的籤文主題
     */
    fun recordQuestionFortuneMapping(
        question: String,
        fortuneContent: String,
        category: QuestionCategory
    ) {
        try {
            val fortuneTheme = MVPSemanticConfig.extractThemeFromFortune(fortuneContent)
            val categoryKey = category.name
            
            if (!extractionData.containsKey(categoryKey)) {
                extractionData[categoryKey] = mutableListOf()
            }
            
            val mapping = mapOf(
                "question" to question,
                "fortuneTheme" to fortuneTheme,
                "category" to categoryKey,
                "timestamp" to System.currentTimeMillis()
            )
            
            extractionData[categoryKey]?.add(JSONObject(mapping).toString())
            
            WtsLogger.i("ReverseExtraction: 記錄問題映射 - 類別=$categoryKey, 主題=$fortuneTheme")
            
        } catch (e: Exception) {
            WtsLogger.e("ReverseExtraction: 記錄失敗 - ${e.message}", e)
        }
    }
    
    /**
     * 保存提取的映射數據到文件
     */
    fun saveExtractionData(filePath: String) {
        try {
            val jsonObject = JSONObject()
            extractionData.forEach { (category, mappings) ->
                val jsonArray = JSONArray()
                mappings.forEach { mapping ->
                    jsonArray.put(JSONObject(mapping))
                }
                jsonObject.put(category, jsonArray)
            }
            
            val file = File(filePath, EXTRACTION_FILE)
            FileWriter(file).use { writer ->
                writer.write(jsonObject.toString(2))
            }
            
            WtsLogger.i("ReverseExtraction: 映射數據已保存到 $file")
            
        } catch (e: Exception) {
            WtsLogger.e("ReverseExtraction: 保存失敗 - ${e.message}", e)
        }
    }
    
    /**
     * 從文件中載入映射數據
     */
    fun loadExtractionData(filePath: String): Map<String, List<Map<String, Any>>> {
        return try {
            val file = File(filePath, EXTRACTION_FILE)
            if (!file.exists()) {
                WtsLogger.w("ReverseExtraction: 映射文件不存在")
                return emptyMap()
            }
            
            val jsonString = file.readText()
            val jsonObject = JSONObject(jsonString)
            val result = mutableMapOf<String, List<Map<String, Any>>>()
            
            jsonObject.keys().forEach { category ->
                val jsonArray = jsonObject.getJSONArray(category)
                val mappings = mutableListOf<Map<String, Any>>()
                
                for (i in 0 until jsonArray.length()) {
                    val mapping = jsonArray.getJSONObject(i)
                    mappings.add(mapOf(
                        "question" to mapping.getString("question"),
                        "fortuneTheme" to mapping.getString("fortuneTheme"),
                        "category" to mapping.getString("category"),
                        "timestamp" to mapping.getLong("timestamp")
                    ))
                }
                
                result[category] = mappings
            }
            
            WtsLogger.i("ReverseExtraction: 載入映射數據 - ${result.size} 個類別")
            result
            
        } catch (e: Exception) {
            WtsLogger.e("ReverseExtraction: 載入失敗 - ${e.message}", e)
            emptyMap()
        }
    }
    
    /**
     * 分析映射數據，生成關鍵詞建議
     */
    fun analyzeMappingData(mappingData: Map<String, List<Map<String, Any>>>): Map<String, List<String>> {
        val keywordSuggestions = mutableMapOf<String, MutableSet<String>>()
        
        mappingData.forEach { (category, mappings) ->
            val keywords = mutableSetOf<String>()
            
            mappings.forEach { mapping ->
                val question = mapping["question"] as? String ?: ""
                val fortuneTheme = mapping["fortuneTheme"] as? String ?: ""
                
                // 從問題中提取關鍵詞
                extractKeywordsFromText(question, keywords)
                
                // 從籤文主題中提取關鍵詞
                extractKeywordsFromText(fortuneTheme, keywords)
            }
            
            keywordSuggestions[category] = keywords.toMutableSet()
        }
        
        return keywordSuggestions.mapValues { it.value.toList() }
    }
    
    /**
     * 從文本中提取關鍵詞
     */
    private fun extractKeywordsFromText(text: String, keywords: MutableSet<String>) {
        // 簡單的關鍵詞提取：移除標點符號，分割詞語
        var cleanText = text
        val punctuations = listOf("，", "。", "！", "？", "、", "；", "：", """, """, "'", "'", "（", "）", "【", "】")
        for (punct in punctuations) {
            cleanText = cleanText.replace(punct, " ")
        }
        val words = cleanText.split(" ").filter { it.length > 1 }
        
        for (word in words) {
            if (word.length in 2..4) { // 只保留2-4字的詞語
                keywords.add(word)
            }
        }
    }
    
    /**
     * 生成關鍵詞匹配表的建議更新
     */
    fun generateKeywordUpdateSuggestions(
        currentKeywords: Map<QuestionCategory, List<String>>,
        extractedKeywords: Map<String, List<String>>
    ): Map<QuestionCategory, List<String>> {
        val suggestions = mutableMapOf<QuestionCategory, List<String>>()
        
        extractedKeywords.forEach { (categoryName, newKeywords) ->
            val category = QuestionCategory.valueOf(categoryName)
            val currentList = currentKeywords[category] ?: emptyList()
            val combinedKeywords = (currentList + newKeywords).distinct().sorted()
            
            suggestions[category] = combinedKeywords
        }
        
        return suggestions
    }
    
    /**
     * 統計映射數據
     */
    fun getMappingStatistics(mappingData: Map<String, List<Map<String, Any>>>): Map<String, Any> {
        val totalMappings = mappingData.values.sumOf { it.size }
        val categoryCounts = mappingData.mapValues { it.value.size }
        
        return mapOf(
            "totalMappings" to totalMappings,
            "categoryCounts" to categoryCounts,
            "lastUpdated" to System.currentTimeMillis()
        )
    }
}
