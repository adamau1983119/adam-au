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
