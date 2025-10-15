package com.wongtaisim.lingqian.data.repository

import com.wongtaisim.lingqian.data.model.*
import com.wongtaisim.lingqian.data.service.QianApiService
import com.wongtaisim.lingqian.integrity.IntegrityVerificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 籤文資料庫存庫
 * 負責管理籤文資料和業務邏輯
 */
@Singleton
class QianRepository @Inject constructor(
    private val qianApiService: QianApiService,
    private val integrityService: IntegrityVerificationService
) {
    
    /**
     * 直接抽籤
     */
    suspend fun drawQian(category: QianCategory): Result<DrawQianResponse> {
        return try {
            // 驗證應用程式完整性
            val isIntegrityValid = integrityService.verifyOperation(
                IntegrityVerificationService.OPERATION_DRAW_QIAN
            )
            
            if (!isIntegrityValid) {
                return Result.failure(Exception("應用程式完整性驗證失敗"))
            }
            
            // 調用API抽籤
            val response = qianApiService.drawQian(
                DrawQianRequest(
                    category = category,
                    integrityToken = null // 實際應該傳遞完整性令牌
                )
            )
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 每日一籤
     */
    suspend fun drawDailyQian(category: QianCategory): Result<DrawQianResponse> {
        return try {
            // 驗證應用程式完整性
            val isIntegrityValid = integrityService.verifyOperation(
                IntegrityVerificationService.OPERATION_DAILY_QIAN
            )
            
            if (!isIntegrityValid) {
                return Result.failure(Exception("應用程式完整性驗證失敗"))
            }
            
            // 檢查今日是否已抽籤
            val dailyStatus = qianApiService.getDailyQianStatus()
            if (dailyStatus.hasDrawnToday) {
                return Result.success(
                    DrawQianResponse(
                        success = false,
                        message = "今日已抽，請明日再來",
                        dailyLimitReached = true
                    )
                )
            }
            
            // 調用API抽籤
            val response = qianApiService.drawQian(
                DrawQianRequest(
                    category = category,
                    integrityToken = null
                )
            )
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 擲杯驗證
     */
    suspend fun validateCupThrowing(results: List<CupResult>): Result<CupValidationResult> {
        return try {
            val positiveCount = results.count { it.result == CupSide.POSITIVE }
            val negativeCount = results.count { it.result == CupSide.NEGATIVE }
            
            val isValid = positiveCount >= 2 || negativeCount >= 2
            val message = if (isValid) {
                "擲杯驗證通過"
            } else {
                "擲杯驗證失敗，請重新抽籤"
            }
            
            Result.success(
                CupValidationResult(
                    isValid = isValid,
                    positiveCount = positiveCount,
                    negativeCount = negativeCount,
                    message = message
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 獲取籤文內容
     */
    suspend fun getQianContent(qianNumber: Int): Result<QianData> {
        return try {
            val qianData = qianApiService.getQianContent(qianNumber)
            Result.success(qianData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 瀏覽籤文列表
     */
    suspend fun getQianList(category: QianCategory? = null): Result<List<QianData>> {
        return try {
            val qianList = qianApiService.getQianList(category)
            Result.success(qianList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 與DeepSeek對話
     */
    suspend fun chatWithDeepSeek(question: String, qianData: QianData): Result<ChatResponse> {
        return try {
            // 驗證應用程式完整性
            val isIntegrityValid = integrityService.verifyOperation(
                IntegrityVerificationService.OPERATION_CHAT
            )
            
            if (!isIntegrityValid) {
                return Result.failure(Exception("應用程式完整性驗證失敗"))
            }
            
            val response = qianApiService.chatWithDeepSeek(
                ChatRequest(
                    question = question,
                    qianData = qianData
                )
            )
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 獲取每日一籤狀態
     */
    suspend fun getDailyQianStatus(): Flow<DailyQianStatus> = flow {
        try {
            val status = qianApiService.getDailyQianStatus()
            emit(status)
        } catch (e: Exception) {
            // 發生錯誤時發送預設狀態
            emit(DailyQianStatus(hasDrawnToday = false))
        }
    }
}
