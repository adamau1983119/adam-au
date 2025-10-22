package com.example.wtsaskingforsignature.config

import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory

/**
 * MVP階段的語義映射簡化配置
 * 使用關鍵詞匹配 + 主題分類表代替複雜的語義向量與聚類
 */
object MVPSemanticConfig {
    
    /**
     * 主題關鍵詞映射表
     * 基於常見用戶問題和靈籤內容反向提取
     */
    val categoryKeywords = mapOf(
        QuestionCategory.CAREER to listOf(
            "升職", "工作", "創業", "老闆", "公司", "事業", "職場", "轉職", "跳槽",
            "升遷", "加薪", "面試", "求職", "就業", "職業", "官祿", "功名", "成就",
            "發展", "前途", "未來", "機會", "挑戰", "競爭", "合作", "團隊", "管理"
        ),
        QuestionCategory.LOVE to listOf(
            "結婚", "配偶", "夫妻", "婚期", "愛情", "感情", "戀愛", "伴侶", "對象",
            "另一半", "戀人", "男友", "女友", "脫單", "桃花", "姻緣", "婚姻", "家庭",
            "約會", "相親", "分手", "復合", "單身", "追求", "表白", "關係", "緣分"
        ),
        QuestionCategory.HEALTH to listOf(
            "身體", "疾病", "康復", "醫療", "健康", "養生", "保健", "治療", "醫生",
            "醫院", "藥品", "手術", "檢查", "症狀", "疼痛", "不適", "體質", "免疫力",
            "運動", "飲食", "睡眠", "壓力", "精神", "心理", "情緒", "疲勞", "活力"
        ),
        QuestionCategory.WEALTH to listOf(
            "財運", "金錢", "投資", "理財", "賺錢", "收入", "支出", "存款", "貸款",
            "股票", "基金", "房產", "生意", "買賣", "交易", "合夥", "合作", "機會",
            "偏財", "正財", "橫財", "破財", "節省", "花費", "購買", "銷售", "利潤"
        ),
        QuestionCategory.GENERAL to listOf(
            "運勢", "運程", "命運", "未來", "發展", "變化", "轉機", "機會", "挑戰",
            "困難", "問題", "解決", "建議", "指引", "方向", "選擇", "決定", "計劃"
        )
    )
    
    /**
     * 籤文內容主題關鍵詞（用於反向提取）
     */
    val fortuneThemeKeywords = mapOf(
        "事業" to listOf("功名", "官祿", "升遷", "成就", "發展", "前途", "機會", "貴人"),
        "愛情" to listOf("婚姻", "姻緣", "桃花", "感情", "夫妻", "和合", "緣分", "伴侶"),
        "健康" to listOf("身體", "疾病", "康復", "健康", "養生", "治療", "病即愈", "平安"),
        "財運" to listOf("求財", "財運", "投資", "理財", "賺錢", "富貴", "豐收", "利潤"),
        "綜合" to listOf("運勢", "運程", "吉凶", "變化", "轉機", "機會", "發展", "未來")
    )
    
    /**
     * 籤文優先邏輯的修飾語句
     */
    val fortunePriorityPhrases = mapOf(
        "positive" to "籤文顯示吉象明顯，可安心前行",
        "negative" to "籤文提醒需謹慎處理，建議保守應對",
        "neutral" to "籤文顯示平穩發展，宜穩步前進",
        "conflict" to "雖命盤顯示略有波動，但籤文顯示吉象明顯，可安心前行"
    )
    
    /**
     * 根據用戶問題匹配主題類別
     */
    fun matchCategory(question: String): QuestionCategory {
        val questionLower = question.lowercase()
        
        // 計算每個類別的匹配分數
        val categoryScores = categoryKeywords.mapValues { (_, keywords) ->
            keywords.count { keyword -> questionLower.contains(keyword.lowercase()) }
        }
        
        // 返回分數最高的類別，如果沒有匹配則返回GENERAL
        val maxScore = categoryScores.values.maxOrNull() ?: 0
        return if (maxScore > 0) {
            categoryScores.entries.find { it.value == maxScore }?.key ?: QuestionCategory.GENERAL
        } else {
            QuestionCategory.GENERAL
        }
    }
    
    /**
     * 從籤文內容中提取主題方向
     */
    fun extractThemeFromFortune(fortuneContent: String): String {
        val contentLower = fortuneContent.lowercase()
        
        val themeScores = fortuneThemeKeywords.mapValues { (_, keywords) ->
            keywords.count { keyword -> contentLower.contains(keyword.lowercase()) }
        }
        
        val maxScore = themeScores.values.maxOrNull() ?: 0
        return if (maxScore > 0) {
            themeScores.entries.find { it.value == maxScore }?.key ?: "綜合"
        } else {
            "綜合"
        }
    }
    
    /**
     * 判斷籤文與命盤是否有衝突
     */
    fun hasConflict(fortuneTheme: String, ziweiAnalysis: String): Boolean {
        // 簡化的衝突檢測：檢查關鍵詞對比
        val fortunePositive = listOf("吉", "好", "成", "利", "順", "旺", "興", "發")
        val fortuneNegative = listOf("凶", "壞", "敗", "不利", "逆", "衰", "困", "破")
        val ziweiPositive = listOf("旺", "吉", "祿", "科", "好", "利")
        val ziweiNegative = listOf("弱", "忌", "凶", "不利", "困")
        
        val fortuneSentiment = if (fortuneTheme.contains("吉") || fortunePositive.any { fortuneTheme.contains(it) }) "positive"
                              else if (fortuneTheme.contains("凶") || fortuneNegative.any { fortuneTheme.contains(it) }) "negative"
                              else "neutral"
                              
        val ziweiSentiment = if (ziweiPositive.any { ziweiAnalysis.contains(it) }) "positive"
                            else if (ziweiNegative.any { ziweiAnalysis.contains(it) }) "negative"
                            else "neutral"
        
        return fortuneSentiment != ziweiSentiment
    }
    
    /**
     * 獲取籤文優先的修飾語句
     */
    fun getFortunePriorityPhrase(hasConflict: Boolean, fortuneSentiment: String): String {
        return if (hasConflict) {
            fortunePriorityPhrases["conflict"] ?: "籤文顯示吉象明顯，可安心前行"
        } else {
            fortunePriorityPhrases[fortuneSentiment] ?: "籤文顯示平穩發展，宜穩步前進"
        }
    }
}
