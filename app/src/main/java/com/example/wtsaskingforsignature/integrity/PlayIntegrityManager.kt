package com.example.wtsaskingforsignature.integrity

import android.content.Context
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManager
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
    
    private var integrityManager: IntegrityManager? = null
    
    companion object {
        private const val TAG = "PlayIntegrityManager"
        private const val CLOUD_PROJECT_NUMBER = 475203L
    }
    
    /**
     * 初始化 Play Integrity Manager
     */
    fun initialize() {
        try {
            integrityManager = IntegrityManagerFactory.create(context)
            Log.d(TAG, "Play Integrity Manager initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Play Integrity Manager", e)
        }
    }
    
    /**
     * 檢查 Play Integrity API 是否可用
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
     * 請求完整性令牌
     */
    suspend fun requestIntegrityToken(nonce: String): Result<IntegrityTokenResponse> {
        return suspendCancellableCoroutine { continuation ->
            if (integrityManager == null) {
                continuation.resumeWithException(Exception("Integrity Manager not initialized"))
                return@suspendCancellableCoroutine
            }
            
            val request = IntegrityTokenRequest.builder()
                .setNonce(nonce)
                .setCloudProjectNumber(CLOUD_PROJECT_NUMBER)
                .build()
            
            integrityManager!!.requestIntegrityToken(request)
                .addOnSuccessListener { response ->
                    Log.d(TAG, "Integrity token request successful")
                    continuation.resume(Result.success(response))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Integrity token request failed", exception)
                    continuation.resume(Result.failure(exception))
                }
        }
    }
    
    /**
     * 驗證應用程式完整性（簡化版本）
     */
    suspend fun verifyAppIntegrity(): Result<Boolean> {
        return try {
            if (!isPlayIntegrityAvailable()) {
                return Result.success(false)
            }
            
            val nonce = generateNonce()
            val tokenResult = requestIntegrityToken(nonce)
            
            tokenResult.fold(
                onSuccess = { response ->
                    Log.d(TAG, "App integrity verification successful")
                    // 這裡應該將令牌發送到後端進行驗證
                    // 目前簡化為直接返回成功
                    Result.success(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "App integrity verification failed", exception)
                    Result.success(false)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "App integrity verification error", e)
            Result.success(false)
        }
    }
    
    /**
     * 生成隨機 nonce
     */
    private fun generateNonce(): String {
        val timestamp = System.currentTimeMillis()
        val random = (Math.random() * 1000000).toInt()
        return "${timestamp}_${random}"
    }
    
    /**
     * 後端驗證（需要實際實現）
     */
    private suspend fun awaitBackendVerification(token: String): Boolean {
        // TODO: 實現後端驗證邏輯
        // 這裡應該將令牌發送到您的後端服務器進行驗證
        Log.d(TAG, "Backend verification not implemented yet")
        return true
    }
}
