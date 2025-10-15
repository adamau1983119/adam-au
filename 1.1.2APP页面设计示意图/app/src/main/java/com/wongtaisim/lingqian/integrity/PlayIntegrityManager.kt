package com.wongtaisim.lingqian.integrity

import android.content.Context
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Play Integrity API 管理器
 * 負責處理應用程式完整性驗證
 */
class PlayIntegrityManager(private val context: Context) {
    
    private val integrityManager = IntegrityManagerFactory.create(context)
    
    companion object {
        private const val TAG = "PlayIntegrityManager"
        
        // 驗證結果狀態
        const val INTEGRITY_SUCCESS = "INTEGRITY_SUCCESS"
        const val INTEGRITY_FAILED = "INTEGRITY_FAILED"
        const val INTEGRITY_ERROR = "INTEGRITY_ERROR"
    }
    
    /**
     * 請求完整性令牌
     * @param nonce 隨機數，用於防止重放攻擊
     * @return 完整性令牌
     */
    suspend fun requestIntegrityToken(nonce: String): String? {
        return try {
            suspendCancellableCoroutine { continuation ->
                val request = IntegrityTokenRequest.builder()
                    .setNonce(nonce)
                    .setCloudProjectNumber(CLOUD_PROJECT_NUMBER) // 需要在 Google Cloud Console 設定
                    .build()
                
                integrityManager.requestIntegrityToken(request)
                    .addOnSuccessListener { response: IntegrityTokenResponse ->
                        Log.d(TAG, "Integrity token request successful")
                        continuation.resume(response.token())
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Integrity token request failed", exception)
                        continuation.resumeWithException(exception)
                    }
                
                continuation.invokeOnCancellation {
                    Log.d(TAG, "Integrity token request cancelled")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting integrity token", e)
            null
        }
    }
    
    /**
     * 驗證應用程式完整性
     * 在關鍵操作前調用此方法
     * @param operation 操作類型（如：抽籤、擲杯等）
     * @return 驗證結果
     */
    suspend fun verifyIntegrity(operation: String): IntegrityResult {
        return try {
            val nonce = generateNonce(operation)
            val token = requestIntegrityToken(nonce)
            
            if (token != null) {
                // 這裡應該將令牌發送到後端進行驗證
                // 後端會使用 Google Play Developer API 驗證令牌
                val isValid = awaitBackendVerification(token, nonce)
                
                if (isValid) {
                    Log.d(TAG, "Integrity verification successful for operation: $operation")
                    IntegrityResult(INTEGRITY_SUCCESS, "驗證成功")
                } else {
                    Log.w(TAG, "Integrity verification failed for operation: $operation")
                    IntegrityResult(INTEGRITY_FAILED, "驗證失敗")
                }
            } else {
                Log.e(TAG, "Failed to obtain integrity token for operation: $operation")
                IntegrityResult(INTEGRITY_ERROR, "無法取得驗證令牌")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during integrity verification", e)
            IntegrityResult(INTEGRITY_ERROR, "驗證過程發生錯誤: ${e.message}")
        }
    }
    
    /**
     * 生成用於驗證的隨機數
     */
    private fun generateNonce(operation: String): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "${operation}_${timestamp}_${random}"
    }
    
    /**
     * 等待後端驗證結果
     * 這裡應該實際調用後端API
     */
    private suspend fun awaitBackendVerification(token: String, nonce: String): Boolean {
        // TODO: 實際實作後端API調用
        // 後端應該使用 Google Play Developer API 驗證令牌
        return true // 暫時返回true，實際應該根據後端驗證結果
    }
    
    /**
     * 檢查是否支援Play Integrity API
     * Play Integrity API 需要 API 21+ 和 Google Play Services
     */
    fun isPlayIntegrityAvailable(): Boolean {
        return try {
            // 檢查 Android 版本
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                Log.w(TAG, "Play Integrity API requires API 21+, current: ${android.os.Build.VERSION.SDK_INT}")
                return false
            }
            
            // 檢查 Google Play Services 是否可用
            val googleApiAvailability = com.google.android.gms.common.GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
                Log.w(TAG, "Google Play Services not available, result code: $resultCode")
                return false
            }
            
            integrityManager != null
        } catch (e: Exception) {
            Log.e(TAG, "Play Integrity API not available", e)
            false
        }
    }
    
    /**
     * 完整性驗證結果
     */
    data class IntegrityResult(
        val status: String,
        val message: String,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    companion object {
        // 需要在 Google Cloud Console 設定您的專案編號
        private const val CLOUD_PROJECT_NUMBER = "475203"
    }
}
