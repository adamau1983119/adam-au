package com.example.wtsaskingforsignature.modules

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisModule
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.data.EnhancedContext.PersonalizedResponse
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.data.EnhancedModels.FortuneMeaning
import com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.PalaceAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.StarAnalysis
import com.example.wtsaskingforsignature.util.WtsLogger

/**
 * 標準範本回答生成器
 * 按照4步驟格式輸出：靈籤基本資訊 → 問題方向+籤文內容 → 紫微斗數盤位分析 → AI整合解籤內容
 */
class StandardTemplateGenerator : AnalysisModule {
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            WtsLogger.i("StandardTemplateGenerator: 開始生成標準範本回答")
            
            val fortuneId = context.fortuneId ?: 1
            val question = context.question
            val ziweiData = context.ziweiData
            val fortuneMeaning = context.metadata["fortuneMeaning"] as? FortuneMeaning
            
            // 生成4步驟標準回答
            val standardResponse = generateStandardTemplateResponse(
                fortuneId = fortuneId,
                question = question,
                ziweiData = ziweiData,
                fortuneMeaning = fortuneMeaning
            )
            
            val personalizedResponse = PersonalizedResponse(
                coreInterpretation = standardResponse,
                ziweiConnection = extractZiweiConnection(ziweiData, question?.category),
                personalizedAdvice = generatePersonalizedAdvice(question?.category),
                timeGuidance = generateTimeGuidance(question?.timeRange),
                fortuneConnection = extractFortuneConnection(fortuneMeaning, question?.category),
                precautions = generatePrecautions(question?.category),
                tone = selectTone(context),
                confidence = calculateConfidence(context)
            )
            
            WtsLogger.i("StandardTemplateGenerator: 標準範本回答生成完成")
            
            AnalysisResult(
                success = true,
                resultCode = "STANDARD_TEMPLATE_SUCCESS",
                data = personalizedResponse,
                processingTime = System.currentTimeMillis(),
                moduleName = "StandardTemplateGenerator"
            )
        } catch (e: Exception) {
            WtsLogger.e("StandardTemplateGenerator: 生成失敗 - ${e.message}", e)
            AnalysisResult(
                success = false,
                resultCode = "STANDARD_TEMPLATE_ERROR",
                errorMessage = e.message,
                processingTime = System.currentTimeMillis(),
                moduleName = "StandardTemplateGenerator"
            )
        }
    }
    
    private fun generateStandardTemplateResponse(
        fortuneId: Int,
        question: com.example.wtsaskingforsignature.data.EnhancedContext.QuestionAnalysis?,
        ziweiData: ZiweiAnalysis?,
        fortuneMeaning: FortuneMeaning?
    ): String {
        return buildString {
            // 靈籤基本資訊
            appendLine("🔮 靈籤基本資訊")
            appendLine()
            appendLine("黃大仙靈籤是：黃大仙靈籤${String.format("%02d", fortuneId)}")
            appendLine("第${String.format("%02d", fortuneId)}靈籤：${fortuneMeaning?.title ?: "未知籤文"}")
            appendLine("求籤吉凶：${fortuneMeaning?.summary ?: "未知吉凶"}")
            appendLine("算命籤詩：")
            val poemContent = extractPoemLines(fortuneMeaning?.content)
            appendLine(poemContent)
            appendLine()
            appendLine("---")
            appendLine()
            
            // 問題方向 + 對應籤文內容
            appendLine("🎯 問題方向 + 對應籤文內容")
            appendLine()
            val personalizedQuestion = generatePersonalizedQuestion(question)
            val fortuneDirection = getFortuneDirection(question?.category, fortuneMeaning)
            appendLine(personalizedQuestion)
            appendLine("籤文顯示：${fortuneDirection}")
            appendLine()
            appendLine("---")
            appendLine()
            
            // 紫微斗數盤位分析（只有在有有效資料時才顯示）
            if (ziweiData != null && hasValidZiweiData(ziweiData)) {
                appendLine("🧭 紫微斗數盤位分析")
                appendLine()
                val palaceAnalysis = generatePalaceAnalysis(ziweiData, question?.category)
                appendLine(palaceAnalysis)
                appendLine()
                appendLine("---")
                appendLine()
            } else {
                appendLine("🧭 紫微斗數盤位分析")
                appendLine()
                appendLine("命盤分析暫不可用，請提供完整的出生資料（出生日期、時間、地點）")
                appendLine("以獲得個人化的紫微斗數分析。")
                appendLine()
                appendLine("---")
                appendLine()
            }
            
            // AI整合解籤內容
            appendLine("🤖 AI整合解籤內容")
            appendLine()
            val aiIntegration = generateAIIntegration(question?.category, fortuneMeaning, ziweiData)
            appendLine(aiIntegration)
        }
    }
    
    /**
     * 根據用戶實際問題生成個性化問題描述
     */
    private fun generatePersonalizedQuestion(question: com.example.wtsaskingforsignature.data.EnhancedContext.QuestionAnalysis?): String {
        if (question == null) {
            return "你現在問的是綜合運勢的問題，"
        }
        
        val rawQuestion = question.originalQuestion ?: ""
        val category = question.category
        val timeRange = question.timeRange ?: ""
        
        return buildString {
            when (category) {
                QuestionCategory.CAREER -> {
                    when {
                        rawQuestion.contains("升職") || rawQuestion.contains("升遷") -> {
                            append("你現在問的是關於升職升遷的問題，")
                        }
                        rawQuestion.contains("轉職") || rawQuestion.contains("換工作") -> {
                            append("你現在問的是關於轉職換工作的問題，")
                        }
                        rawQuestion.contains("創業") || rawQuestion.contains("開公司") -> {
                            append("你現在問的是關於創業開公司的問題，")
                        }
                        rawQuestion.contains("工作") || rawQuestion.contains("職場") -> {
                            append("你現在問的是關於工作職場的問題，")
                        }
                        rawQuestion.contains("事業") -> {
                            append("你現在問的是關於事業發展的問題，")
                        }
                        else -> {
                            append("你現在問的是關於事業方向的問題，")
                        }
                    }
                }
                QuestionCategory.LOVE -> {
                    when {
                        rawQuestion.contains("分手") || rawQuestion.contains("離婚") -> {
                            append("你現在問的是關於感情分手的問題，")
                        }
                        rawQuestion.contains("結婚") || rawQuestion.contains("婚禮") -> {
                            append("你現在問的是關於結婚婚禮的問題，")
                        }
                        rawQuestion.contains("戀愛") || rawQuestion.contains("交往") -> {
                            append("你現在問的是關於戀愛交往的問題，")
                        }
                        rawQuestion.contains("桃花") || rawQuestion.contains("單身") -> {
                            append("你現在問的是關於桃花運勢的問題，")
                        }
                        rawQuestion.contains("感情") || rawQuestion.contains("愛情") -> {
                            append("你現在問的是關於感情愛情的問題，")
                        }
                        else -> {
                            append("你現在問的是關於感情關係的問題，")
                        }
                    }
                }
                QuestionCategory.HEALTH -> {
                    when {
                        rawQuestion.contains("生病") || rawQuestion.contains("疾病") -> {
                            append("你現在問的是關於身體健康狀況的問題，")
                        }
                        rawQuestion.contains("懷孕") || rawQuestion.contains("生育") -> {
                            append("你現在問的是關於懷孕生育的問題，")
                        }
                        rawQuestion.contains("手術") || rawQuestion.contains("開刀") -> {
                            append("你現在問的是關於手術治療的問題，")
                        }
                        rawQuestion.contains("養生") || rawQuestion.contains("保健") -> {
                            append("你現在問的是關於養生保健的問題，")
                        }
                        rawQuestion.contains("健康") -> {
                            append("你現在問的是關於身體健康的問題，")
                        }
                        else -> {
                            append("你現在問的是關於健康狀況的問題，")
                        }
                    }
                }
                QuestionCategory.WEALTH -> {
                    when {
                        rawQuestion.contains("投資") || rawQuestion.contains("股票") -> {
                            append("你現在問的是關於投資理財的問題，")
                        }
                        rawQuestion.contains("買房") || rawQuestion.contains("置業") -> {
                            append("你現在問的是關於買房置業的問題，")
                        }
                        rawQuestion.contains("借錢") || rawQuestion.contains("借貸") -> {
                            append("你現在問的是關於借貸財務的問題，")
                        }
                        rawQuestion.contains("賺錢") || rawQuestion.contains("收入") -> {
                            append("你現在問的是關於賺錢收入的問題，")
                        }
                        rawQuestion.contains("財運") || rawQuestion.contains("金錢") -> {
                            append("你現在問的是關於財運金錢的問題，")
                        }
                        else -> {
                            append("你現在問的是關於財運投資的問題，")
                        }
                    }
                }
                QuestionCategory.GENERAL -> {
                    when {
                        rawQuestion.contains("運勢") || rawQuestion.contains("運程") -> {
                            append("你現在問的是關於整體運勢的問題，")
                        }
                        rawQuestion.contains("未來") || rawQuestion.contains("發展") -> {
                            append("你現在問的是關於未來發展的問題，")
                        }
                        rawQuestion.contains("選擇") || rawQuestion.contains("決定") -> {
                            append("你現在問的是關於人生選擇的問題，")
                        }
                        else -> {
                            append("你現在問的是關於綜合運勢的問題，")
                        }
                    }
                }
                else -> {
                    append("你現在問的是關於綜合運勢的問題，")
                }
            }
            
            // 添加時間範圍信息（如果有）
            if (timeRange.isNotEmpty() && timeRange != "一般時期") {
                append("時間範圍是${timeRange}，")
            }
        }
    }
    
    private fun getCategoryText(category: QuestionCategory?): String {
        return when (category) {
            QuestionCategory.CAREER -> "事業方向"
            QuestionCategory.LOVE -> "愛情感情"
            QuestionCategory.HEALTH -> "健康狀況"
            QuestionCategory.WEALTH -> "財運投資"
            QuestionCategory.GENERAL -> "綜合運勢"
            null -> "綜合運勢"
        }
    }
    
    private fun getFortuneDirection(category: QuestionCategory?, fortuneMeaning: FortuneMeaning?): String {
        val content = fortuneMeaning?.content ?: ""
        return when (category) {
            QuestionCategory.CAREER -> {
                when {
                    content.contains("貴人") -> "由於有很多貴人扶持，謀事較易成就"
                    content.contains("升遷") || content.contains("官祿") -> "事業發展順利，有升遷機會"
                    content.contains("創業") -> "適合創業或開拓新領域"
                    else -> "事業運勢平穩，需要耐心經營"
                }
            }
            QuestionCategory.LOVE -> {
                when {
                    content.contains("婚姻") || content.contains("姻緣") -> "感情發展順利，有機會遇到心儀對象"
                    content.contains("桃花") -> "桃花運旺盛，感情機會增加"
                    content.contains("和合") -> "感情關係和諧，適合進一步發展"
                    else -> "感情運勢平穩，需要主動把握機會"
                }
            }
            QuestionCategory.HEALTH -> {
                when {
                    content.contains("病即愈") -> "健康狀況良好，疾病將得到改善"
                    content.contains("身體") -> "身體狀況穩定，注意保養"
                    else -> "健康運勢平穩，注意日常保健"
                }
            }
            QuestionCategory.WEALTH -> {
                when {
                    content.contains("求財豐") -> "財運亨通，正財偏財都有機會"
                    content.contains("投資") -> "投資運勢良好，但需謹慎選擇"
                    else -> "財運平穩，需要理性理財"
                }
            }
            else -> "運勢整體平穩，需要耐心等待時機"
        }
    }
    
    private fun generatePalaceAnalysis(ziweiData: ZiweiAnalysis?, category: QuestionCategory?): String {
        if (ziweiData == null) {
            return "命盤分析暫不可用，請提供完整的出生資料（出生日期、時間、地點）"
        }
        
        // 驗證是否有基本的命盤資料
        if (!hasValidZiweiData(ziweiData)) {
            return "命盤資料不完整，無法進行準確分析。請確認已提供正確的出生資料。"
        }
        
        val palace = getRelevantPalace(ziweiData, category)
        val mainStar = palace?.mainStar ?: "未知主星"
        val palaceName = getPalaceName(category)
        val siHuaAnalysis = generateSiHuaAnalysis(palace)
        val timeGuidance = generateTimeGuidanceForPalace(category, palace)
        
        return buildString {
            appendLine("根據你的紫微斗數命盤，${getCategoryText(category)}對應的主星為：${mainStar}，位於${palaceName}。")
            appendLine("此宮位在2025年11月-12月期間的流年運勢顯示：${siHuaAnalysis}，代表${timeGuidance}。")
        }
    }
    
    private fun getRelevantPalace(ziweiData: ZiweiAnalysis, category: QuestionCategory?): PalaceAnalysis? {
        return when (category) {
            QuestionCategory.CAREER -> ziweiData.careerPalace
            QuestionCategory.LOVE -> ziweiData.lovePalace
            QuestionCategory.HEALTH -> ziweiData.healthPalace
            QuestionCategory.WEALTH -> ziweiData.wealthPalace
            else -> ziweiData.mingGong?.let { 
                PalaceAnalysis(
                    mainStar = it.mainStar,
                    secondaryStars = it.secondaryStars,
                    siHua = it.siHua,
                    strength = ""
                )
            }
        }
    }
    
    private fun getPalaceName(category: QuestionCategory?): String {
        return when (category) {
            QuestionCategory.CAREER -> "官祿宮"
            QuestionCategory.LOVE -> "夫妻宮"
            QuestionCategory.HEALTH -> "疾厄宮"
            QuestionCategory.WEALTH -> "財帛宮"
            else -> "命宮"
        }
    }
    
    private fun generateSiHuaAnalysis(palace: PalaceAnalysis?): String {
        if (palace == null) return "星曜配置平穩"
        
        val siHua = palace.siHua
        val mainStar = palace.mainStar
        
        return buildString {
            when {
                siHua.contains("祿") && siHua.contains("科") -> {
                    append("有「化祿」與「化科」星同時入主，代表貴人運強且學識名聲佳")
                }
                siHua.contains("祿") -> {
                    append("有「化祿」星入主，代表財運亨通，貴人運強")
                }
                siHua.contains("科") -> {
                    append("有「化科」星入主，代表學識名聲提升，考試運佳")
                }
                siHua.contains("忌") -> {
                    append("有「化忌」星影響，代表此期間需要特別謹慎，避免衝動決策")
                }
                else -> {
                    append("星曜配置平穩，無特殊四化星影響")
                }
            }
            
            // 根據主星特質添加額外分析
            when (mainStar) {
                "天府星" -> append("，天府星主穩重，適合穩健發展")
                "天相星" -> append("，天相星主輔佐，適合合作共事")
                "武曲星" -> append("，武曲星主財富，適合理財投資")
                "天同星" -> append("，天同星主享受，適合休閒養生")
                "太陽星" -> append("，太陽星主名聲，適合公開表現")
                "太陰星" -> append("，太陰星主內斂，適合幕後工作")
                "天梁星" -> append("，天梁星主長壽，適合長期規劃")
                "七殺星" -> append("，七殺星主開創，適合突破創新")
                "破軍星" -> append("，破軍星主變化，適合轉型改革")
                "貪狼星" -> append("，貪狼星主慾望，適合積極進取")
                "紫微星" -> append("，紫微星主領導，適合統籌管理")
                "天機星" -> append("，天機星主智慧，適合學習思考")
                else -> append("")
            }
        }
    }
    
    private fun generateTimeGuidanceForPalace(category: QuestionCategory?, palace: PalaceAnalysis?): String {
        if (palace == null) {
            return when (category) {
                QuestionCategory.CAREER -> "事業運勢平穩，適合穩步發展"
                QuestionCategory.LOVE -> "感情運勢穩定，需要耐心經營"
                QuestionCategory.HEALTH -> "健康狀況平穩，注意日常保養"
                QuestionCategory.WEALTH -> "財運平穩，適合穩健理財"
                else -> "運勢整體平穩，適合穩步發展"
            }
        }
        
        val mainStar = palace.mainStar
        val siHua = palace.siHua
        
        return buildString {
            when (category) {
                QuestionCategory.CAREER -> {
                    when {
                        siHua.contains("祿") && mainStar == "天府星" -> {
                            append("事業發展順利，有升遷機會，適合爭取重要職位")
                        }
                        siHua.contains("科") && mainStar == "太陽星" -> {
                            append("名聲運勢佳，適合公開表現，考試或評比容易成功")
                        }
                        siHua.contains("忌") && mainStar == "七殺星" -> {
                            append("事業需要謹慎，避免重大決策，專注於穩固現有基礎")
                        }
                        mainStar == "武曲星" -> {
                            append("財運與事業相輔相成，適合與金錢相關的工作")
                        }
                        mainStar == "天相星" -> {
                            append("適合輔佐他人，合作共事將帶來好運")
                        }
                        else -> {
                            append("事業運勢平穩，根據個人特質調整發展方向")
                        }
                    }
                }
                QuestionCategory.LOVE -> {
                    when {
                        siHua.contains("祿") && mainStar == "天同星" -> {
                            append("感情運勢佳，容易遇到心儀對象，關係發展順利")
                        }
                        siHua.contains("科") && mainStar == "天相星" -> {
                            append("透過學習和成長能增進感情，適合一起參與活動")
                        }
                        siHua.contains("忌") -> {
                            append("感情需要謹慎處理，避免衝動決定，多溝通了解")
                        }
                        mainStar == "貪狼星" -> {
                            append("桃花運勢強，但需要理性選擇，避免感情糾紛")
                        }
                        mainStar == "太陰星" -> {
                            append("感情較為內斂，需要耐心培養，適合細水長流的關係")
                        }
                        else -> {
                            append("感情運勢平穩，需要用心經營，真誠相待")
                        }
                    }
                }
                QuestionCategory.HEALTH -> {
                    when {
                        siHua.contains("祿") && mainStar == "天梁星" -> {
                            append("健康運勢良好，身體狀況穩定，適合進行養生保健")
                        }
                        siHua.contains("忌") -> {
                            append("健康需要特別注意，定期檢查，避免過度勞累")
                        }
                        mainStar == "天同星" -> {
                            append("適合休閒養生，注意飲食均衡，保持規律作息")
                        }
                        mainStar == "七殺星" -> {
                            append("需要適度運動，避免過度激烈，注意意外傷害")
                        }
                        else -> {
                            append("健康狀況平穩，保持良好生活習慣即可")
                        }
                    }
                }
                QuestionCategory.WEALTH -> {
                    when {
                        siHua.contains("祿") && mainStar == "武曲星" -> {
                            append("財運亨通，正財偏財都有機會，適合積極投資")
                        }
                        siHua.contains("科") && mainStar == "天機星" -> {
                            append("透過智慧和技能能增加收入，適合學習新技能")
                        }
                        siHua.contains("忌") -> {
                            append("財運需要謹慎，避免高風險投資，專注於穩健理財")
                        }
                        mainStar == "天府星" -> {
                            append("適合穩健理財，長期投資將有不錯回報")
                        }
                        mainStar == "貪狼星" -> {
                            append("財運機會多，但需要理性判斷，避免貪心")
                        }
                        else -> {
                            append("財運平穩，適合穩健的理財方式")
                        }
                    }
                }
                else -> {
                    when {
                        siHua.contains("祿") -> {
                            append("整體運勢良好，各方面都有發展機會")
                        }
                        siHua.contains("科") -> {
                            append("學習運勢佳，適合提升自我，增加知識技能")
                        }
                        siHua.contains("忌") -> {
                            append("需要謹慎行事，避免重大決策，穩步發展")
                        }
                        else -> {
                            append("運勢整體平穩，適合穩步發展")
                        }
                    }
                }
            }
        }
    }
    
    private fun generateAIIntegration(
        category: QuestionCategory?,
        fortuneMeaning: FortuneMeaning?,
        ziweiData: ZiweiAnalysis?
    ): String {
        val categoryText = getCategoryText(category)
        val fortuneDirection = getFortuneDirection(category, fortuneMeaning)
        
        return buildString {
            // 根據是否有有效的命盤資料來決定分析內容
            if (ziweiData != null && hasValidZiweiData(ziweiData)) {
                appendLine("綜合靈籤內容與紫微斗數命盤分析，2025年11月-12月你的${categoryText}運勢分析如下：")
                appendLine("靈籤顯示${fortuneDirection}；結合你的個人命盤特質，建議如下：")
                appendLine()
                
                // 根據實際籤文內容和命盤分析生成個性化建議
                val personalizedAdvice = generatePersonalizedAdvice(category, fortuneMeaning, ziweiData)
                appendLine(personalizedAdvice)
            } else {
                appendLine("基於靈籤內容分析，2025年11月-12月你的${categoryText}運勢如下：")
                appendLine("籤文顯示${fortuneDirection}。")
                appendLine()
                
                // 基於籤文內容生成建議
                val fortuneBasedAdvice = generateFortuneBasedAdvice(category, fortuneMeaning)
                appendLine(fortuneBasedAdvice)
                appendLine()
                appendLine("注意：如需更精確的個人化分析，請提供完整的出生資料（出生日期、時間、地點）。")
            }
        }
    }
    
    private fun extractZiweiConnection(ziweiData: ZiweiAnalysis?, category: QuestionCategory?): String {
        val palace = getRelevantPalace(ziweiData ?: return "", category)
        return palace?.let { "紫微斗數分析：${it.mainStar}星位於${getPalaceName(category)}" } ?: ""
    }
    
    private fun generatePersonalizedAdvice(category: QuestionCategory?): List<String> {
        return when (category) {
            QuestionCategory.CAREER -> listOf(
                "積極參與重要項目",
                "主動爭取資源與曝光機會",
                "善用人脈資源"
            )
            QuestionCategory.LOVE -> listOf(
                "保持真誠態度",
                "多參與社交活動",
                "把握感情機會"
            )
            QuestionCategory.HEALTH -> listOf(
                "注意日常保健",
                "保持規律作息",
                "適度運動"
            )
            QuestionCategory.WEALTH -> listOf(
                "制定理財計劃",
                "避免高風險投資",
                "注重長期收益"
            )
            else -> listOf(
                "保持積極心態",
                "把握機會",
                "穩步發展"
            )
        }
    }
    
    private fun generateTimeGuidance(timeRange: String?): String {
        return timeRange?.let { "時間指引：$it" } ?: "時間指引：2025年11月-12月"
    }
    
    private fun extractFortuneConnection(fortuneMeaning: FortuneMeaning?, @Suppress("UNUSED_PARAMETER") category: QuestionCategory?): String {
        return fortuneMeaning?.let { "籤文寓意：${it.title}" } ?: ""
    }
    
    private fun generatePrecautions(category: QuestionCategory?): List<String> {
        return when (category) {
            QuestionCategory.CAREER -> listOf("避免衝動決策", "謹慎處理人際關係")
            QuestionCategory.LOVE -> listOf("避免過於急躁", "注意溝通方式")
            QuestionCategory.HEALTH -> listOf("避免過度勞累", "注意飲食健康")
            QuestionCategory.WEALTH -> listOf("避免高風險投資", "理性理財")
            else -> listOf("保持理性思考", "避免衝動決策")
        }
    }
    
    /**
     * 根據籤文內容和命盤分析生成個性化建議
     */
    private fun generatePersonalizedAdvice(
        category: QuestionCategory?,
        fortuneMeaning: FortuneMeaning?,
        ziweiData: ZiweiAnalysis?
    ): String {
        val categoryText = getCategoryText(category)
        val fortuneContent = fortuneMeaning?.content ?: ""
        val fortuneSummary = fortuneMeaning?.summary ?: ""
        
        // 分析籤文的情感傾向
        val fortuneSentiment = analyzeFortuneSentiment(fortuneContent, fortuneSummary)
        
        // 獲取相關宮位分析
        val palace = getRelevantPalace(ziweiData ?: return "", category)
        val mainStar = palace?.mainStar ?: ""
        val siHua = palace?.siHua ?: ""
        
        return buildString {
            when (category) {
                QuestionCategory.CAREER -> {
                    when {
                        fortuneSentiment == "positive" && siHua.contains("祿") -> {
                            appendLine("籤文顯示貴人運強，命盤又有化祿星加持，這是事業發展的絕佳時機。")
                            appendLine("建議：積極爭取升遷機會，主動承擔重要項目，善用貴人資源，將有望在年底前取得突破性進展。")
                        }
                        fortuneSentiment == "positive" && siHua.contains("科") -> {
                            appendLine("籤文顯示運勢轉好，命盤有化科星入主，代表學識和名聲將帶來機會。")
                            appendLine("建議：專注提升專業技能，參與培訓或考證，透過知識和專業能力來推動事業發展。")
                        }
                        fortuneSentiment == "negative" && siHua.contains("忌") -> {
                            appendLine("籤文提醒需要謹慎，命盤顯示化忌星影響，代表此期間需要特別注意。")
                            appendLine("建議：避免重大決策，專注於穩固現有基礎，等待更適合的時機再進行擴展。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，結合你的${mainStar}星特質，需要平衡發展。")
                            appendLine("建議：保持穩健步伐，根據籤文指引調整策略，將有助於事業的長期發展。")
                        }
                    }
                }
                QuestionCategory.LOVE -> {
                    when {
                        fortuneSentiment == "positive" && siHua.contains("祿") -> {
                            appendLine("籤文顯示感情運勢良好，命盤有化祿星加持，代表感情機會增加。")
                            appendLine("建議：主動表達心意，多參與社交活動，把握機會與心儀對象建立更深的連結。")
                        }
                        fortuneSentiment == "positive" && siHua.contains("科") -> {
                            appendLine("籤文顯示感情發展順利，命盤有化科星入主，代表透過學習和成長能增進感情。")
                            appendLine("建議：與伴侶一起學習新技能或參與共同興趣，透過共同成長來深化關係。")
                        }
                        fortuneSentiment == "negative" -> {
                            appendLine("籤文提醒感情需要耐心，命盤顯示需要謹慎處理感情問題。")
                            appendLine("建議：避免衝動決定，多溝通了解彼此想法，給彼此一些空間和時間。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，結合你的${mainStar}星特質，感情需要用心經營。")
                            appendLine("建議：保持真誠態度，根據籤文指引調整相處方式，將有助於感情的穩定發展。")
                        }
                    }
                }
                QuestionCategory.HEALTH -> {
                    when {
                        fortuneSentiment == "positive" -> {
                            appendLine("籤文顯示健康運勢良好，身體狀況將有改善。")
                            appendLine("建議：保持規律作息，適度運動，注意飲食均衡，將有助於身體健康。")
                        }
                        fortuneSentiment == "negative" -> {
                            appendLine("籤文提醒需要注意健康，身體可能出現小問題。")
                            appendLine("建議：定期健康檢查，避免過度勞累，如有不適應及時就醫，預防勝於治療。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，健康需要持續關注。")
                            appendLine("建議：保持健康的生活習慣，根據籤文指引調整生活方式，將有助於身體健康。")
                        }
                    }
                }
                QuestionCategory.WEALTH -> {
                    when {
                        fortuneSentiment == "positive" && siHua.contains("祿") -> {
                            appendLine("籤文顯示財運亨通，命盤有化祿星加持，代表正財偏財都有機會。")
                            appendLine("建議：積極把握投資機會，但需謹慎選擇，避免貪心，穩健理財將帶來豐厚回報。")
                        }
                        fortuneSentiment == "positive" && siHua.contains("科") -> {
                            appendLine("籤文顯示財運穩定，命盤有化科星入主，代表透過知識和技能能增加收入。")
                            appendLine("建議：投資自己的技能提升，考慮副業或兼職，透過專業能力來增加收入來源。")
                        }
                        fortuneSentiment == "negative" -> {
                            appendLine("籤文提醒需要謹慎理財，避免不必要的開支。")
                            appendLine("建議：制定詳細的預算計劃，避免高風險投資，專注於穩健的理財方式。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，財運需要理性規劃。")
                            appendLine("建議：根據籤文指引調整理財策略，保持穩健的投資態度，將有助於財富的累積。")
                        }
                    }
                }
                else -> {
                    appendLine("籤文顯示${fortuneSummary}，結合你的個人特質，需要綜合考量。")
                    appendLine("建議：保持積極態度，根據籤文指引調整策略，將有助於各方面的發展與突破。")
                }
            }
        }
    }
    
    /**
     * 基於籤文內容生成建議（無命盤資料時）
     */
    private fun generateFortuneBasedAdvice(
        category: QuestionCategory?,
        fortuneMeaning: FortuneMeaning?
    ): String {
        val categoryText = getCategoryText(category)
        val fortuneContent = fortuneMeaning?.content ?: ""
        val fortuneSummary = fortuneMeaning?.summary ?: ""
        
        val fortuneSentiment = analyzeFortuneSentiment(fortuneContent, fortuneSummary)
        
        return buildString {
            when (category) {
                QuestionCategory.CAREER -> {
                    when (fortuneSentiment) {
                        "positive" -> {
                            appendLine("籤文顯示事業運勢良好，有貴人相助，謀事易成。")
                            appendLine("建議：積極把握機會，主動爭取資源，善用人脈關係，將有助於事業發展。")
                        }
                        "negative" -> {
                            appendLine("籤文提醒事業需要謹慎，避免衝動決策。")
                            appendLine("建議：保持穩健態度，專注於現有工作，等待更適合的時機再進行改變。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，事業需要平衡發展。")
                            appendLine("建議：根據籤文指引調整策略，保持積極態度，將有助於事業的穩定發展。")
                        }
                    }
                }
                QuestionCategory.LOVE -> {
                    when (fortuneSentiment) {
                        "positive" -> {
                            appendLine("籤文顯示感情運勢良好，有機會遇到心儀對象。")
                            appendLine("建議：主動表達心意，多參與社交活動，把握機會建立感情連結。")
                        }
                        "negative" -> {
                            appendLine("籤文提醒感情需要耐心，避免過於急躁。")
                            appendLine("建議：保持真誠態度，多溝通了解彼此想法，給感情一些時間發展。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，感情需要用心經營。")
                            appendLine("建議：根據籤文指引調整相處方式，保持積極態度，將有助於感情的發展。")
                        }
                    }
                }
                QuestionCategory.HEALTH -> {
                    when (fortuneSentiment) {
                        "positive" -> {
                            appendLine("籤文顯示健康運勢良好，身體狀況將有改善。")
                            appendLine("建議：保持規律作息，適度運動，注意飲食均衡，將有助於身體健康。")
                        }
                        "negative" -> {
                            appendLine("籤文提醒需要注意健康，身體可能出現小問題。")
                            appendLine("建議：定期健康檢查，避免過度勞累，如有不適應及時就醫。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，健康需要持續關注。")
                            appendLine("建議：保持健康的生活習慣，根據籤文指引調整生活方式。")
                        }
                    }
                }
                QuestionCategory.WEALTH -> {
                    when (fortuneSentiment) {
                        "positive" -> {
                            appendLine("籤文顯示財運亨通，正財偏財都有機會。")
                            appendLine("建議：積極把握投資機會，但需謹慎選擇，避免貪心，穩健理財。")
                        }
                        "negative" -> {
                            appendLine("籤文提醒需要謹慎理財，避免不必要的開支。")
                            appendLine("建議：制定詳細的預算計劃，避免高風險投資，專注於穩健的理財方式。")
                        }
                        else -> {
                            appendLine("籤文顯示${fortuneSummary}，財運需要理性規劃。")
                            appendLine("建議：根據籤文指引調整理財策略，保持穩健的投資態度。")
                        }
                    }
                }
                else -> {
                    appendLine("籤文顯示${fortuneSummary}，需要綜合考量各方面因素。")
                    appendLine("建議：保持積極態度，根據籤文指引調整策略，將有助於各方面的發展。")
                }
            }
        }
    }
    
    /**
     * 分析籤文的情感傾向
     */
    private fun analyzeFortuneSentiment(content: String, summary: String): String {
        val positiveKeywords = listOf("吉", "好", "成", "利", "順", "旺", "興", "發", "上上", "中上", "貴人", "機會", "成功")
        val negativeKeywords = listOf("凶", "壞", "敗", "不利", "逆", "衰", "困", "破", "下下", "中下", "謹慎", "注意", "避免")
        
        val fullText = "$content $summary"
        val positiveCount = positiveKeywords.count { fullText.contains(it) }
        val negativeCount = negativeKeywords.count { fullText.contains(it) }
        
        return when {
            positiveCount > negativeCount -> "positive"
            negativeCount > positiveCount -> "negative"
            else -> "neutral"
        }
    }
    
    /**
     * 驗證紫微斗數資料是否有效
     */
    private fun hasValidZiweiData(ziweiData: ZiweiAnalysis): Boolean {
        // 檢查是否有基本的命宮資料
        val mingGong = ziweiData.mingGong
        if (mingGong == null || mingGong.mainStar.isBlank()) {
            return false
        }
        
        // 檢查是否有至少一個宮位的資料
        val hasAnyPalace = listOf(
            ziweiData.careerPalace,
            ziweiData.lovePalace,
            ziweiData.wealthPalace,
            ziweiData.healthPalace
        ).any { it != null && it.mainStar.isNotBlank() }
        
        return hasAnyPalace
    }
    
    /**
     * 提取籤詩的前兩行詩句
     */
    private fun extractPoemLines(content: String?): String {
        if (content.isNullOrBlank()) return "籤詩內容暫不可用"
        
        // 移除可能的標題和說明文字，只保留詩句
        val cleanContent = content
            .substringBefore("黃大仙算命解籤詩：")
            .substringBefore("詩人")
            .substringBefore("此籤")
            .trim()
        
        // 按行分割，過濾空行
        val lines = cleanContent.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.contains("：") && !it.contains("是") }
        
        // 取前兩行詩句
        return when {
            lines.size >= 2 -> "${lines[0]}\n${lines[1]}"
            lines.size == 1 -> lines[0]
            else -> "籤詩內容格式異常"
        }
    }
    
    private fun selectTone(context: AnalysisContext): com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone {
        val userProfile = context.userProfile
        return when {
            userProfile?.age ?: 0 < 30 -> com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone.ENCOURAGING
            userProfile?.age ?: 0 > 50 -> com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone.WARM
            context.question?.category == QuestionCategory.CAREER -> com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone.PROFESSIONAL
            else -> com.example.wtsaskingforsignature.data.EnhancedContext.ResponseTone.DIRECT
        }
    }
    
    private fun calculateConfidence(context: AnalysisContext): Float {
        var confidence = 0.5f
        
        if (context.ziweiData != null) confidence += 0.2f
        if (context.fortuneId != null) confidence += 0.2f
        if (context.question?.timeRange?.isNotEmpty() == true) confidence += 0.1f
        
        return minOf(confidence, 1.0f)
    }
}
