// 時間窗口分析模組
class TimeWindowAnalyzer : AnalysisModule {
    private val timeRangeParser = TimeRangeParser()
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            val timeRange = context.question?.timeRange ?: ""
            val parsedRange = timeRangeParser.parseTimeRange(timeRange)
            val monthlyAnalysis = analyzeMonthlyTrends(parsedRange, context.ziweiData!!)
            
            AnalysisResult(
                success = true,
                resultCode = "TIME_ANALYSIS_SUCCESS",
                data = monthlyAnalysis,
                processingTime = System.currentTimeMillis(),
                moduleName = "TimeWindowAnalyzer"
            )
        } catch (e: Exception) {
            AnalysisResult(
                success = false,
                resultCode = "TIME_ANALYSIS_ERROR",
                errorMessage = e.message,
                processingTime = System.currentTimeMillis(),
                moduleName = "TimeWindowAnalyzer"
            )
        }
    }
    
    private fun analyzeMonthlyTrends(
        parsedRange: ParsedTimeRange,
        ziweiData: ZiweiAnalysis
    ): MonthlyAnalysis {
        return when {
            parsedRange.startMonth == 10 && parsedRange.endMonth == 12 -> {
                MonthlyAnalysis(
                    october = generateOctoberGuidance(ziweiData),
                    november = generateNovemberGuidance(ziweiData),
                    december = generateDecemberGuidance(ziweiData)
                )
            }
            parsedRange.year == 2025 -> {
                MonthlyAnalysis(
                    october = "穩步發展，保持現狀",
                    november = "積極表現，把握機會", 
                    december = "總結成果，規劃未來"
                )
            }
            else -> MonthlyAnalysis(
                october = "穩步發展",
                november = "積極表現",
                december = "總結規劃"
            )
        }
    }
    
    private fun generateOctoberGuidance(ziweiData: ZiweiAnalysis): String {
        return when {
            ziweiData.careerPalace.hasHuaJi() -> "宜穩守，避免衝動決策"
            ziweiData.careerPalace.hasHuaLu() -> "穩步發展，把握機會"
            else -> "穩步發展，保持現狀"
        }
    }
    
    private fun generateNovemberGuidance(ziweiData: ZiweiAnalysis): String {
        return when {
            ziweiData.careerPalace.hasHuaKe() -> "表現機會多，適合爭取上級認可"
            ziweiData.careerPalace.hasHuaLu() -> "積極表現，把握機會"
            else -> "積極表現，把握機會"
        }
    }
    
    private fun generateDecemberGuidance(ziweiData: ZiweiAnalysis): String {
        return when {
            ziweiData.careerPalace.hasHuaLu() -> "有升遷或轉職契機"
            ziweiData.careerPalace.hasHuaKe() -> "總結成果，規劃未來"
            else -> "總結成果，規劃未來"
        }
    }
}
