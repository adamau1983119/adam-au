package com.example.wtsaskingforsignature.ziwei

import com.example.wtsaskingforsignature.data.api.ChatMessage
import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.data.api.DrawResponse
import com.example.wtsaskingforsignature.util.WtsLogger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 紫薇斗数分析适配器
 * 将Java紫薇斗数系统集成到Kotlin架构中
 */
class ZiweiAnalysisAdapter {
    
    private val analyzer = ZiweiAnalyzer()
    
    /**
     * 分析籤文与紫薇斗数
     * @param fortune 籤文信息
     * @param question 用户问题
     * @param personalInfo 个人资料字符串（格式：生日,出生地,性别）
     * @return 聊天响应
     */
    suspend fun analyzeWithZiwei(
        fortune: DrawResponse,
        question: String,
        personalInfo: String
    ): ChatResponse {
        return try {
            WtsLogger.i("开始紫薇斗数分析：籤文ID=${fortune.id}")
            
            // 解析个人资料
            val parsedInfo = parsePersonalInfo(personalInfo)
            
            // 创建籤文信息
            val fortuneInfo = ZiweiAnalyzer.FortuneInfo(
                fortune.id,
                fortune.title ?: "第${fortune.id}籤",
                fortune.summary ?: "籤文",
                fortune.content ?: "籤文内容"
            )
            
            // 进行紫薇斗数分析
            val analysisResult = analyzer.analyze(fortuneInfo, parsedInfo)
            
            // 生成聊天消息
            val messages = listOf(
                ChatMessage(
                    role = "user",
                    content = question
                ),
                ChatMessage(
                    role = "assistant",
                    content = formatAnalysisResult(analysisResult)
                )
            )
            
            ChatResponse(messages = messages)
            
        } catch (e: Exception) {
            WtsLogger.e("紫薇斗数分析失败：${e.message}")
            
            // 出错时返回基础分析
            val fallbackMessage = "紫薇斗数分析暂时无法提供，请稍后再试。\n\n籤文分析：第${fortune.id}籤 - ${fortune.title ?: ""}\n\n建议您保持积极心态，相信自己的判断。"
            
            val messages = listOf(
                ChatMessage(
                    role = "user",
                    content = question
                ),
                ChatMessage(
                    role = "assistant",
                    content = fallbackMessage
                )
            )
            
            ChatResponse(messages = messages)
        }
    }
    
    /**
     * 解析个人资料字符串
     * 格式：生日,出生地,性别
     * 示例：1990-01-01 12:00,北京,男
     */
    private fun parsePersonalInfo(personalInfo: String): ZiweiAnalyzer.PersonalInfo {
        val parts = personalInfo.split(",")
        
        val birthDateStr = parts.getOrNull(0)?.trim() ?: "1990-01-01 12:00"
        val birthPlace = parts.getOrNull(1)?.trim() ?: "未知"
        val gender = parts.getOrNull(2)?.trim() ?: "未知"
        
        // 解析生日
        val birthDate = try {
            if (birthDateStr.contains(":")) {
                // 包含时间的格式
                LocalDateTime.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            } else {
                // 只有日期的格式，默认中午12点
                LocalDateTime.parse("$birthDateStr 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            }
        } catch (e: Exception) {
            WtsLogger.w("生日解析失败，使用默认值：$birthDateStr")
            LocalDateTime.of(1990, 1, 1, 12, 0)
        }
        
        return ZiweiAnalyzer.PersonalInfo(birthDate, birthPlace, gender, "")
    }
    
    /**
     * 格式化分析结果
     */
    private fun formatAnalysisResult(result: ZiweiAnalyzer.AnalysisResult): String {
        val sb = StringBuilder()
        
        sb.append("🔮 **紫薇斗数深度分析**\n\n")
        
        // 籤文分析
        sb.append("📜 **籤文分析**\n")
        sb.append(result.fortuneAnalysis).append("\n")
        
        // 紫薇斗数分析
        sb.append("⭐ **紫薇斗数分析**\n")
        sb.append(result.ziweiAnalysis).append("\n")
        
        // 结合分析
        sb.append("🔗 **结合分析**\n")
        sb.append(result.combinationAnalysis).append("\n")
        
        // 个性化建议
        sb.append("💡 **个性化建议**\n")
        sb.append(result.personalizedAdvice).append("\n")
        
        // 技术说明
        sb.append("---\n")
        sb.append("💻 *此分析由内置紫薇斗数系统生成，无需网络连接*")
        
        return sb.toString()
    }
    
    /**
     * 快速分析（简化版）
     */
    suspend fun quickAnalyze(
        fortuneId: Int,
        question: String,
        birthDate: String
    ): String {
        return try {
            val fortune = DrawResponse(
                id = fortuneId,
                title = "第${fortuneId}籤",
                summary = "籤文",
                content = "籤文内容"
            )
            
            val personalInfo = "$birthDate,未知,未知"
            val result = analyzeWithZiwei(fortune, question, personalInfo)
            
            result.messages.lastOrNull()?.content ?: "分析失败"
            
        } catch (e: Exception) {
            "快速分析失败：${e.message}"
        }
    }
    
    /**
     * 获取紫薇斗数基本信息
     */
    fun getBasicZiweiInfo(birthDate: String): String {
        return try {
            val personalInfo = parsePersonalInfo("$birthDate,未知,未知")
            val calculator = ZiweiCalculator()
            val result = calculator.calculate(personalInfo.birthDate, personalInfo.birthPlace)
            
            """
            🔮 **紫薇斗数基本信息**
            
            📅 出生时间：${personalInfo.birthDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"))}
            🏠 出生地点：${personalInfo.birthPlace}
            
            🎯 **核心信息**
            • 命宫：${result.mingGongDizhi}宫 (${calculator.getGongWeiName(result.mingGong)})
            • 身宫：${result.shenGongDizhi}宫 (${calculator.getGongWeiName(result.shenGong)})
            • 紫微星：${calculator.getGongWeiName(result.mainStars["紫微"] ?: 0)}宫
            
            💡 *此信息基于内置紫薇斗数算法计算*
            """.trimIndent()
            
        } catch (e: Exception) {
            "紫薇斗数信息获取失败：${e.message}"
        }
    }
}
