package com.wongtaisim.lingqian.data.service

import com.wongtaisim.lingqian.data.model.*
import retrofit2.http.*

/**
 * 籤文API服務接口
 */
interface QianApiService {
    
    /**
     * 直接抽籤
     */
    @POST("api/qian/draw")
    suspend fun drawQian(@Body request: DrawQianRequest): DrawQianResponse
    
    /**
     * 獲取籤文內容
     */
    @GET("api/qian/{number}")
    suspend fun getQianContent(@Path("number") number: Int): QianData
    
    /**
     * 獲取籤文列表
     */
    @GET("api/qian/list")
    suspend fun getQianList(@Query("category") category: QianCategory? = null): List<QianData>
    
    /**
     * 與DeepSeek對話
     */
    @POST("api/chat/deepseek")
    suspend fun chatWithDeepSeek(@Body request: ChatRequest): ChatResponse
    
    /**
     * 獲取每日一籤狀態
     */
    @GET("api/qian/daily-status")
    suspend fun getDailyQianStatus(): DailyQianStatus
    
    /**
     * 驗證擲杯結果
     */
    @POST("api/qian/validate-cup")
    suspend fun validateCupThrowing(@Body results: List<CupResult>): CupValidationResult
}
