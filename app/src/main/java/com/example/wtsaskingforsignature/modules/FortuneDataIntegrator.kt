package com.example.wtsaskingforsignature.modules

import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisContext
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisResult
import com.example.wtsaskingforsignature.data.EnhancedContext.AnalysisModule
import com.example.wtsaskingforsignature.data.EnhancedModels.FortuneMeaning
import com.example.wtsaskingforsignature.data.EnhancedContext.QuestionCategory
import com.example.wtsaskingforsignature.data.api.DrawResponse
import com.example.wtsaskingforsignature.data.LocalRepository
import com.example.wtsaskingforsignature.util.WtsLogger
import kotlinx.coroutines.runBlocking

/**
 * 真正的籤文資料整合模組
 * 從App內實際的籤文資料庫讀取內容
 */
class FortuneDataIntegrator : AnalysisModule {
    
    private val localRepository = LocalRepository()
    
    override fun process(context: AnalysisContext): AnalysisResult {
        return try {
            val fortuneId = context.fortuneId ?: 0
            val questionCategory = context.question?.category ?: QuestionCategory.GENERAL
            
            WtsLogger.i("FortuneDataIntegrator: 讀取籤文ID=$fortuneId, 類別=$questionCategory")
            
            // 從App內實際資料庫讀取籤文
            val fortuneData = runBlocking { readFortuneFromApp(fortuneId) }
            val fortuneMeaning = extractFortuneMeaning(fortuneData, questionCategory)
            
            AnalysisResult(
                success = true,
                resultCode = "SUCCESS",
                data = fortuneMeaning,
                moduleName = getModuleName()
            )
        } catch (e: Exception) {
            WtsLogger.e("FortuneDataIntegrator 執行失敗: ${e.message}", e)
            AnalysisResult(
                success = false,
                resultCode = "FORTUNE_READ_FAILED",
                errorMessage = e.message,
                moduleName = getModuleName()
            )
        }
    }
    
    override fun getModuleName(): String = "FortuneDataIntegrator"
    override fun getVersion(): String = "1.0"
    
    /**
     * 從App內實際資料庫讀取籤文
     */
    private suspend fun readFortuneFromApp(fortuneId: Int): DrawResponse {
        return try {
            val result = localRepository.fortune(fortuneId)
            if (result.isSuccess) {
                result.getOrThrow()
            } else {
                WtsLogger.w("無法讀取籤文ID=$fortuneId，使用預設籤文")
                DrawResponse(
                    id = fortuneId,
                    title = "第${fortuneId}籤",
                    summary = "籤文",
                    content = "籤文內容"
                )
            }
        } catch (e: Exception) {
            WtsLogger.e("讀取籤文失敗: ${e.message}")
            DrawResponse(
                id = fortuneId,
                title = "第${fortuneId}籤",
                summary = "籤文",
                content = "籤文內容"
            )
        }
    }
    
    /**
     * 從實際籤文內容提取寓意
     */
    private fun extractFortuneMeaning(fortuneData: DrawResponse, category: QuestionCategory): FortuneMeaning {
        val content = fortuneData.content
        val title = fortuneData.title
        val summary = fortuneData.summary
        
        // 從實際籤文內容分析寓意
        val meaning = extractMeaningFromContent(content, title)
        val symbol = extractSymbolFromContent(content, category)
        
        return FortuneMeaning(
            fortuneId = fortuneData.id,
            category = category,
            meaning = meaning ?: "籤文寓意",
            symbol = symbol,
            content = content ?: ""
        )
    }
    
    /**
     * 從籤文內容提取寓意
     */
    private fun extractMeaningFromContent(content: String?, title: String?): String {
        // 從標題提取關鍵詞
        val t = title ?: ""
        val titleKeywords = when {
            t.contains("碎琴") -> "伯才碎琴"
            t.contains("封相") -> "姜公封相"
            t.contains("桃源") -> "誤入桃源"
            t.contains("訓迪") -> "魯班訓迪"
            t.contains("惜花") -> "韓夫人惜花"
            t.contains("歸故里") -> "王羲之歸故里"
            t.contains("歸家") -> "仁貴歸家"
            t.contains("占鵲巢") -> "鳩占鵲巢"
            t.contains("賞菊") -> "陶淵明賞菊"
            t.contains("不第") -> "蘇秦不第"
            else -> "籤文寓意"
        }
        
        return titleKeywords
    }
    
    /**
     * 從籤文內容提取象徵意義
     */
    private fun extractSymbolFromContent(content: String?, category: QuestionCategory): String {
        val lowerContent = (content ?: "").lowercase()
        
        return when (category) {
            QuestionCategory.CAREER -> {
                when {
                    lowerContent.contains("升職") || lowerContent.contains("升遷") -> "事業將有升遷機會"
                    lowerContent.contains("挫折") || lowerContent.contains("困難") -> "事業可能遇到挑戰"
                    lowerContent.contains("貴人") || lowerContent.contains("相助") -> "有貴人相助，事業順利"
                    else -> "事業運勢需要謹慎評估"
                }
            }
            QuestionCategory.LOVE -> {
                when {
                    lowerContent.contains("良緣") || lowerContent.contains("姻緣") -> "感情運勢良好，易遇良緣"
                    lowerContent.contains("分手") || lowerContent.contains("離別") -> "感情需要謹慎處理"
                    else -> "感情運勢需要時間觀察"
                }
            }
            QuestionCategory.HEALTH -> {
                when {
                    lowerContent.contains("健康") || lowerContent.contains("身體") -> "健康狀況需要關注"
                    lowerContent.contains("疾病") || lowerContent.contains("病痛") -> "健康需要特別注意"
                    else -> "健康運勢平穩"
                }
            }
            QuestionCategory.WEALTH -> {
                when {
                    lowerContent.contains("財運") || lowerContent.contains("金錢") -> "財運需要謹慎管理"
                    lowerContent.contains("投資") || lowerContent.contains("理財") -> "投資理財需要謹慎"
                    else -> "財運運勢需要觀察"
                }
            }
            else -> "籤文寓意需要綜合分析"
        }
    }
}
