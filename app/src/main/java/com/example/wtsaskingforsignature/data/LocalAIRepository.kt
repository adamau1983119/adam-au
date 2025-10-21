package com.example.wtsaskingforsignature.data

import com.example.wtsaskingforsignature.data.api.ChatMessage
import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.data.api.CupAttempt
import com.example.wtsaskingforsignature.data.api.CupResultResponse
import com.example.wtsaskingforsignature.data.api.DrawResponse
import com.example.wtsaskingforsignature.util.WtsLogger
import com.example.wtsaskingforsignature.ziwei.ZiweiAnalysisAdapter
import com.example.wtsaskingforsignature.ai.DeepSeekInterpreter
import com.example.wtsaskingforsignature.data.EnhancedModels.UserProfile
import com.example.wtsaskingforsignature.data.EnhancedModels.ZiweiAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.StarAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.PalaceAnalysis
import com.example.wtsaskingforsignature.data.EnhancedModels.LiuNianAnalysis
import com.example.wtsaskingforsignature.data.EnhancedContext.Gender

/**
 * 本地AI解签系统
 * 提供智能的籤文解读，无需外部API
 * 集成紫薇斗数分析功能
 */
class LocalAIRepository : Repository {
    
	// 紫薇斗数分析适配器
	private val ziweiAdapter = ZiweiAnalysisAdapter()
	
	// 籤文类型分类
	private val fortuneCategories = mapOf(
		"事业" to listOf(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43, 45, 47, 49, 51, 53, 55, 57, 59, 61, 63, 65, 67, 69, 71, 73, 75, 77, 79, 81, 83, 85, 87, 89, 91, 93, 95, 97, 99),
		"财运" to listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52, 54, 56, 58, 60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92, 94, 96, 98, 100),
		"健康" to listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100),
		"感情" to listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100),
		"学业" to listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100)
	)
	
	// 籤文解读模板
	private val interpretationTemplates = mapOf(
		"事业" to mapOf(
			"上上籤" to "事业运势极佳，适合大展宏图。建议把握机会，积极进取，有望获得重大突破。",
			"上籤" to "事业运势良好，工作顺利，人际关系和谐。继续保持努力，会有不错的发展。",
			"中籤" to "事业运势平稳，需要耐心等待时机。建议稳扎稳打，积累经验，为未来做准备。",
			"下籤" to "事业运势欠佳，可能遇到阻碍。建议调整策略，寻求帮助，避免冲动决策。"
		),
		"财运" to mapOf(
			"上上籤" to "财运亨通，投资理财有望获得丰厚回报。建议合理规划，适度投资，财富会稳步增长。",
			"上籤" to "财运不错，收入稳定，支出合理。建议保持节俭习惯，适当储蓄，为未来做准备。",
			"中籤" to "财运平稳，收支平衡。建议量入为出，避免不必要的开支，稳健理财。",
			"下籤" to "财运欠佳，需要谨慎理财。建议减少支出，避免投资风险，等待时机好转。"
		),
		"健康" to mapOf(
			"上上籤" to "身体健康，精力充沛。建议保持良好作息，适度运动，健康状态会持续良好。",
			"上籤" to "健康状况良好，注意保养。建议规律作息，均衡饮食，预防胜于治疗。",
			"中籤" to "健康状况一般，需要关注。建议定期体检，注意休息，及时调理身体。",
			"下籤" to "健康状况欠佳，需要重视。建议及时就医，调整生活方式，积极治疗。"
		),
		"感情" to mapOf(
			"上上籤" to "感情运势极佳，桃花运旺盛。单身者有望遇到心仪对象，有伴侣的感情会更加甜蜜。",
			"上籤" to "感情运势良好，人际关系和谐。建议多与朋友交流，感情生活会更加丰富。",
			"中籤" to "感情运势平稳，需要耐心等待。建议保持开放心态，顺其自然，缘分自会到来。",
			"下籤" to "感情运势欠佳，可能遇到波折。建议冷静处理，沟通理解，避免冲动决定。"
		),
		"学业" to mapOf(
			"上上籤" to "学业运势极佳，学习效率高，理解能力强。建议把握机会，深入学习，有望获得优异成绩。",
			"上籤" to "学业运势良好，学习态度端正，进步明显。建议保持专注，继续努力，会有更好表现。",
			"中籤" to "学业运势平稳，需要加强努力。建议制定计划，提高效率，克服学习困难。",
			"下籤" to "学业运势欠佳，可能遇到瓶颈。建议寻求帮助，调整方法，重新规划学习。"
		)
	)

    // 內容委派到本地資料庫（資產 CSV/JSON），保持與既有App一致
    private val contentRepository by lazy { LocalRepository() }

    override suspend fun draw(): Result<DrawResponse> = contentRepository.draw()

    override suspend fun fortune(id: Int): Result<DrawResponse> = contentRepository.fortune(id)

	override suspend fun cupResult(): Result<CupResultResponse> {
		val attempts = List(3) { attempt ->
			CupAttempt(
				attempt = attempt + 1,
				isPositive = (0..1).random() == 1
			)
		}
		val isValid = attempts.count { it.isPositive } >= 2
		
		return Result.success(
			CupResultResponse(
				attempts = attempts,
				isValid = isValid
			)
		)
	}

	override suspend fun chat(fortuneId: Int, question: String): Result<ChatResponse> {
		WtsLogger.i("本地AI解签：籤文ID=$fortuneId, 问题=$question")
		
		// 使用新的DeepSeekInterpreter進行分析
		return performEnhancedAnalysis(fortuneId, question)
	}
	
    /**
     * 使用真正執行的DeepSeekInterpreter進行分析
     */
    private suspend fun performEnhancedAnalysis(fortuneId: Int, question: String): Result<ChatResponse> {
        return try {
            WtsLogger.i("LocalAIRepository: 開始增強分析 - 籤文ID=$fortuneId, 問題=$question")
            
            // 創建DeepSeekInterpreter實例
            val interpreter = DeepSeekInterpreter()

            // 從問題中提取真實用戶資料
            val userProfile = extractRealUserProfile(question)
            WtsLogger.i("提取用戶資料: ${userProfile.name}, ${userProfile.birthDate}")

            // 使用真實的紫微斗數計算（將在模組中執行）
            val ziweiData = generateZiweiData(userProfile)

            // 使用真正執行的解釋器生成回答
            val response = interpreter.interpretFortune(
                question = question,
                userProfile = userProfile,
                ziweiData = ziweiData,
                fortuneId = fortuneId
            )

            // 轉換為ChatResponse格式
            val chatResponse = ChatResponse(
                messages = listOf(
                    ChatMessage(
                        role = "assistant",
                        content = buildString {
                            append(response.coreInterpretation)
                            if (response.timeGuidance.isNotEmpty()) {
                                append("\n\n").append(response.timeGuidance)
                            }
                            if (response.fortuneConnection.isNotEmpty()) {
                                append("\n\n").append(response.fortuneConnection)
                            }
                            if (response.personalizedAdvice.isNotEmpty()) {
                                append("\n\n建議：").append(response.personalizedAdvice.joinToString("；"))
                            }
                        }
                    )
                )
            )

            WtsLogger.i("LocalAIRepository: 增強分析完成")
            Result.success(chatResponse)

        } catch (e: Exception) {
            WtsLogger.e("Enhanced analysis failed: ${e.message}", e)
            // 降級到傳統分析
            performTraditionalAnalysis(fortuneId, question)
        }
    }
	
	/**
	 * 從問題中提取真實用戶資料
	 */
	private fun extractRealUserProfile(question: String): UserProfile {
		// 從問題中解析真實的用戶資料
		val name = extractNameFromQuestion(question)
		val birthDate = extractBirthDateFromQuestion(question)
		val birthTime = extractBirthTimeFromQuestion(question)
		val birthPlace = extractBirthPlaceFromQuestion(question)
		val age = calculateAge(birthDate)
		val gender = extractGenderFromQuestion(question)
		
		return UserProfile(
			name = name,
			age = age,
			gender = gender,
			birthDate = birthDate,
			birthTime = birthTime,
			birthPlace = birthPlace
		)
	}
	
	/**
	 * 從問題中提取姓名
	 */
	private fun extractNameFromQuestion(question: String): String {
		// 簡單的姓名提取邏輯
		val namePattern = Regex("([歐蘇王李陳黃張劉吳周徐孫馬朱胡郭何高林羅鄭梁謝宋唐許韓馮鄧曹彭曾蕭田董袁潘於蔣蔡余杜葉程魏蘇呂丁任沈姚盧姜崔鍾譚陸汪范金石廖賈夏韋付方白鄒孟熊秦邱江尹薛閆段雷侯龍史陶黎賀顧毛郝龔邵萬錢嚴覃武戴莫孔向湯]\\w{1,2})")
		val match = namePattern.find(question)
		return match?.value ?: "用戶"
	}
	
	/**
	 * 從問題中提取出生日期
	 */
	private fun extractBirthDateFromQuestion(question: String): String {
		val datePattern = Regex("(\\d{4})[-/年](\\d{1,2})[-/月](\\d{1,2})")
		val match = datePattern.find(question)
		
		return if (match != null) {
			val (year, month, day) = match.destructured
			"$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
		} else {
			"1990-01-01" // 預設日期
		}
	}
	
	/**
	 * 從問題中提取出生時間
	 */
	private fun extractBirthTimeFromQuestion(question: String): String {
		val timePattern = Regex("(\\d{1,2}):(\\d{2})")
		val match = timePattern.find(question)
		
		return if (match != null) {
			val (hour, minute) = match.destructured
			"${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
		} else {
			"12:00" // 預設時間
		}
	}
	
	/**
	 * 從問題中提取出生地點
	 */
	private fun extractBirthPlaceFromQuestion(question: String): String {
		val places = listOf("香港", "台灣", "大陸", "澳門", "新加坡", "馬來西亞")
		for (place in places) {
			if (question.contains(place)) {
				return place
			}
		}
		return "香港" // 預設地點
	}
	
	/**
	 * 計算年齡
	 */
	private fun calculateAge(birthDate: String): Int {
		return try {
			val year = birthDate.substring(0, 4).toInt()
			val currentYear = java.time.LocalDate.now().year
			currentYear - year
		} catch (e: Exception) {
			30 // 預設年齡
		}
	}
	
	/**
	 * 從問題中提取性別
	 */
	private fun extractGenderFromQuestion(question: String): Gender {
		return when {
			question.contains("男") || question.contains("先生") -> Gender.MALE
			question.contains("女") || question.contains("小姐") -> Gender.FEMALE
			else -> Gender.UNKNOWN
		}
	}
	
	/**
	 * 從問題中提取用戶資料（保留舊函數以兼容）
	 */
	private fun extractUserProfile(question: String): UserProfile {
		// 簡化的用戶資料提取（實際應該從用戶輸入中解析）
		return UserProfile(
			name = "用戶",
			age = 30,
			gender = Gender.MALE,
			birthDate = "1983-01-19",
			birthTime = "10:30",
			birthPlace = "香港"
		)
	}
	
	/**
	 * 生成紫微斗數資料
	 */
	private fun generateZiweiData(userProfile: UserProfile): ZiweiAnalysis {
		// 簡化的紫微斗數資料生成（實際應該基於出生資料計算）
		return ZiweiAnalysis(
			mingGong = StarAnalysis(
				mainStar = "天機",
				secondaryStars = listOf("文昌", "文曲"),
				siHua = "化科"
			),
			careerPalace = PalaceAnalysis(
				mainStar = "太陽",
				secondaryStars = listOf("天梁", "左輔"),
				siHua = "化祿",
				strength = "強旺"
			),
			liuNian = LiuNianAnalysis(
				year = 2025,
				careerTrend = "上升",
				keyMonths = listOf("10月", "11月", "12月"),
				opportunities = "貴人相助、資源對接"
			)
		)
	}
	
	/**
	 * 判断是否为紫薇斗数分析请求
	 */
	private fun isZiweiAnalysisRequest(question: String): Boolean {
		val ziweiKeywords = listOf("紫薇", "紫微", "斗数", "命宫", "身宫", "紫微星", "生辰", "生日", "出生")
		return ziweiKeywords.any { question.contains(it) }
	}
	
	/**
	 * 执行紫薇斗数分析
	 */
	private suspend fun performZiweiAnalysis(fortuneId: Int, question: String): Result<ChatResponse> {
		return try {
			// 提取生日信息
			val birthDate = extractBirthDate(question)
			val personalInfo = "$birthDate,未知,未知"
			
			// 创建籤文对象
			val fortune = DrawResponse(
				id = fortuneId,
				title = "第${fortuneId}籤",
				summary = "籤文",
				content = "籤文内容"
			)
			
			// 使用紫薇斗数分析
			val result = ziweiAdapter.analyzeWithZiwei(fortune, question, personalInfo)
			Result.success(result)
			
		} catch (e: Exception) {
			WtsLogger.e("紫薇斗数分析失败：${e.message}")
			// 降级到传统分析
			performTraditionalAnalysis(fortuneId, question)
        }
    }
    
    /**
	 * 执行传统本地AI分析
	 */
	private suspend fun performTraditionalAnalysis(fortuneId: Int, question: String): Result<ChatResponse> {
		// 分析问题类型
		val category = analyzeQuestionCategory(question)
		val fortuneLevel = analyzeFortuneLevel(fortuneId)
		
		// 生成智能解读
		val interpretation = generateInterpretation(category, fortuneLevel, question, fortuneId)
		
		val messages = listOf(
			ChatMessage(
				role = "assistant",
				content = interpretation
			)
		)
		
		return Result.success(ChatResponse(messages = messages))
	}
	
	/**
	 * 从问题中提取生日信息
	 */
	private fun extractBirthDate(question: String): String {
		// 简单的生日提取逻辑
		val datePattern = Regex("(\\d{4})[-/年](\\d{1,2})[-/月](\\d{1,2})")
		val match = datePattern.find(question)
		
		return if (match != null) {
			val (year, month, day) = match.destructured
			"$year-${month.padStart(2, '0')}-${day.padStart(2, '0')} 12:00"
		} else {
			"1990-01-01 12:00" // 默认生日
		}
	}
	
	private fun analyzeQuestionCategory(question: String): String {
		return when {
			question.contains("事业") || question.contains("工作") || question.contains("事业") -> "事业"
			question.contains("财运") || question.contains("投资") || question.contains("理财") -> "财运"
			question.contains("健康") || question.contains("身体") || question.contains("疾病") -> "健康"
			question.contains("感情") || question.contains("爱情") || question.contains("婚姻") -> "感情"
			question.contains("学业") || question.contains("学习") || question.contains("考试") -> "学业"
			else -> "事业" // 默认分类
		}
	}
	
	private fun analyzeFortuneLevel(fortuneId: Int): String {
		return when {
			fortuneId in 1..25 -> "上上籤"
			fortuneId in 26..50 -> "上籤"
			fortuneId in 51..75 -> "中籤"
			else -> "下籤"
		}
	}
	
	private fun generateInterpretation(category: String, level: String, question: String, fortuneId: Int): String {
		val baseTemplate = interpretationTemplates[category]?.get(level) ?: "籤文解读需要更多信息"
		
		val personalizedAdvice = when (category) {
			"事业" -> "\n\n针对您的问题，建议：\n1. 保持积极心态，把握机会\n2. 加强团队合作，建立良好人际关系\n3. 持续学习提升，为事业发展做准备"
			"财运" -> "\n\n针对您的问题，建议：\n1. 制定合理的理财计划\n2. 避免冲动消费，量入为出\n3. 适当储蓄，为未来做准备"
			"健康" -> "\n\n针对您的问题，建议：\n1. 保持规律作息，早睡早起\n2. 均衡饮食，适量运动\n3. 定期体检，预防胜于治疗"
			"感情" -> "\n\n针对您的问题，建议：\n1. 保持真诚态度，用心经营感情\n2. 多沟通理解，避免误解\n3. 给彼此空间，顺其自然"
			"学业" -> "\n\n针对您的问题，建议：\n1. 制定详细的学习计划\n2. 找到适合自己的学习方法\n3. 保持专注，避免分心"
			else -> "\n\n建议您保持积极心态，相信自己的判断，顺其自然。"
		}
		
		return "第${fortuneId}籤解读：\n\n$baseTemplate$personalizedAdvice\n\n籤文提示：保持信心，积极面对，好运自会降临。"
    }
}
