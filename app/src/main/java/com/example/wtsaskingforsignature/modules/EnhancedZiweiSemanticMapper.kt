// 增強的紫微語意映射模組
class EnhancedZiweiSemanticMapper : AnalysisModule {
    private val professionalTermGenerator = ProfessionalTermGenerator()
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            val professionalExplanation = generateProfessionalExplanation(context)
            val fortuneIntegration = integrateFortuneMeaning(context)
            
            val result = buildString {
                append(professionalExplanation)
                if (fortuneIntegration.isNotEmpty()) {
                    append("。$fortuneIntegration")
                }
            }
            
            AnalysisResult(
                success = true,
                resultCode = "SEMANTIC_MAPPING_SUCCESS",
                data = result,
                processingTime = System.currentTimeMillis(),
                moduleName = "EnhancedZiweiSemanticMapper"
            )
        } catch (e: Exception) {
            AnalysisResult(
                success = false,
                resultCode = "SEMANTIC_MAPPING_ERROR",
                errorMessage = e.message,
                processingTime = System.currentTimeMillis(),
                moduleName = "EnhancedZiweiSemanticMapper"
            )
        }
    }
    
    private fun generateProfessionalExplanation(context: AnalysisContext): String {
        val ziweiData = context.ziweiData ?: return "命盤分析暫不可用"
        val explanations = mutableListOf<String>()
        
        // 事業宮專業解釋
        val careerPalace = ziweiData.careerPalace
        if (careerPalace != null) {
            when {
                careerPalace.hasHuaLu() -> explanations.add("事業宮見吉星化祿")
                careerPalace.hasHuaKe() -> explanations.add("流年官祿宮有貴人星輔助")
                careerPalace.hasHuaJi() -> explanations.add("事業宮見化忌，需謹慎處理")
            }
        }
        
        // 命宮分析
        val mingPalace = ziweiData.mingGong
        if (mingPalace != null) {
            when {
                mingPalace.hasHuaLu() -> explanations.add("命宮見吉星化祿")
                mingPalace.hasHuaKe() -> explanations.add("命宮有貴人星輔助")
            }
        }
        
        val baseExplanation = explanations.joinToString("，")
        return if (baseExplanation.isNotEmpty()) {
            "$baseExplanation，顯示年底工作運勢漸入佳境"
        } else {
            "命盤顯示工作運勢平穩"
        }
    }
    
    private fun integrateFortuneMeaning(context: AnalysisContext): String {
        val fortuneId = context.fortuneId ?: return ""
        val category = context.question?.category ?: return ""
        
        val fortuneMeaning = TerminologyConfig.fortuneMeanings[fortuneId]?.get(category)
        return fortuneMeaning?.let { meaning ->
            // 直接引用籤文內容來支持邏輯
            "第${fortuneId}籤寓意「${meaning.meaning}」，暗示${meaning.symbol}。籤文「${meaning.content}」"
        } ?: ""
    }
}
