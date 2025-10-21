package com.example.wtsaskingforsignature.config

import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.data.EnhancedModels.FortuneMeaning

// 專業術語配置
object TerminologyConfig {
    val careerTerms = mapOf(
        "化祿" to "吉星化祿",
        "化科" to "貴人星輔助",
        "化忌" to "化忌",
        "事業宮" to "事業宮",
        "官祿宮" to "流年官祿宮"
    )
    
    val fortuneMeanings = mapOf(
        1 to mapOf(
            QuestionCategory.CAREER to FortuneMeaning(
                fortuneId = 1,
                category = QuestionCategory.CAREER,
                meaning = "雲開見日",
                symbol = "否極泰來，年底將迎來事業突破"
            ),
            QuestionCategory.LOVE to FortuneMeaning(
                fortuneId = 1,
                category = QuestionCategory.LOVE,
                meaning = "雲開見日",
                symbol = "感情運勢轉好，易遇良緣"
            ),
            QuestionCategory.HEALTH to FortuneMeaning(
                fortuneId = 1,
                category = QuestionCategory.HEALTH,
                meaning = "雲開見日",
                symbol = "健康狀況改善，身體轉好"
            )
        ),
        10 to mapOf(
            QuestionCategory.CAREER to FortuneMeaning(
                fortuneId = 10,
                category = QuestionCategory.CAREER,
                meaning = "時來運轉",
                symbol = "工作運勢將有轉機，年底將迎來事業突破"
            ),
            QuestionCategory.LOVE to FortuneMeaning(
                fortuneId = 10,
                category = QuestionCategory.LOVE,
                meaning = "時來運轉",
                symbol = "感情運勢轉好，易遇良緣"
            ),
            QuestionCategory.HEALTH to FortuneMeaning(
                fortuneId = 10,
                category = QuestionCategory.HEALTH,
                meaning = "時來運轉",
                symbol = "健康狀況改善，身體轉好"
            )
        ),
        40 to mapOf(
            QuestionCategory.CAREER to FortuneMeaning(
                fortuneId = 40,
                category = QuestionCategory.CAREER,
                meaning = "伯才碎琴",
                symbol = "失友失物、才華無人賞識、百事不利",
                content = "人世知音能有幾 碎琴都為子期亡 墳前洒盡千行淚 隔別陰陽各一方"
            ),
            QuestionCategory.LOVE to FortuneMeaning(
                fortuneId = 40,
                category = QuestionCategory.LOVE,
                meaning = "伯才碎琴",
                symbol = "感情中需要調整心態，不要被悲傷影響",
                content = "人世知音能有幾 碎琴都為子期亡 墳前洒盡千行淚 隔別陰陽各一方"
            ),
            QuestionCategory.HEALTH to FortuneMeaning(
                fortuneId = 40,
                category = QuestionCategory.HEALTH,
                meaning = "伯才碎琴",
                symbol = "身體狀況一般，注意保持規律作息",
                content = "人世知音能有幾 碎琴都為子期亡 墳前洒盡千行淚 隔別陰陽各一方"
            )
        )
    )
    
    val monthlyGuidance = mapOf(
        "10月" to mapOf(
            "穩守" to "宜穩守，避免衝動",
            "發展" to "穩步發展，保持現狀"
        ),
        "11月" to mapOf(
            "機會" to "表現機會多，適合爭取上級認可",
            "表現" to "積極表現，把握機會"
        ),
        "12月" to mapOf(
            "升遷" to "有升遷或轉職契機",
            "規劃" to "總結成果，規劃未來"
        )
    )
}
