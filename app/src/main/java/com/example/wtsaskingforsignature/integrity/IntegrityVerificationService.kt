package com.example.wtsaskingforsignature.integrity

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 完整性驗證服務
 * 負責管理應用程式完整性驗證流程
 */
class IntegrityVerificationService(private val context: Context) {
    
    private val playIntegrityManager = PlayIntegrityManager(context)
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val TAG = "IntegrityVerificationService"
    }
    
    /**
     * 初始化完整性驗證
     */
    fun initializeIntegrity() {
        serviceScope.launch {
            try {
                playIntegrityManager.initialize()
                Log.d(TAG, "Integrity verification service initialized")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize integrity verification service", e)
            }
        }
    }
    
    /**
     * 檢查設備兼容性
     */
    fun isDeviceCompatible(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
    }
    
    /**
     * 獲取兼容性描述
     */
    fun getCompatibilityDescription(): String {
        return when {
            !isDeviceCompatible() -> "此設備不支援Play Integrity API（需要Android 5.0+）"
            !playIntegrityManager.isPlayIntegrityAvailable() -> "Google Play Services不可用"
            else -> "設備完全兼容"
        }
    }
    
    /**
     * 執行完整性驗證
     */
    suspend fun performIntegrityCheck(): IntegrityCheckResult {
        return try {
            if (!isDeviceCompatible()) {
                return IntegrityCheckResult(
                    isSuccess = false,
                    message = "設備不支援Play Integrity API",
                    isCompatible = false
                )
            }
            
            if (!playIntegrityManager.isPlayIntegrityAvailable()) {
                return IntegrityCheckResult(
                    isSuccess = false,
                    message = "Google Play Services不可用",
                    isCompatible = true
                )
            }
            
            val verificationResult = playIntegrityManager.verifyAppIntegrity()
            verificationResult.fold(
                onSuccess = { isValid ->
                    IntegrityCheckResult(
                        isSuccess = isValid,
                        message = if (isValid) "應用程式完整性驗證成功" else "應用程式完整性驗證失敗",
                        isCompatible = true
                    )
                },
                onFailure = { exception ->
                    IntegrityCheckResult(
                        isSuccess = false,
                        message = "完整性驗證過程中發生錯誤: ${exception.message}",
                        isCompatible = true
                    )
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Integrity check failed", e)
            IntegrityCheckResult(
                isSuccess = false,
                message = "完整性驗證失敗: ${e.message}",
                isCompatible = true
            )
        }
    }
    
    /**
     * 獲取 Play Integrity Manager
     */
    fun getPlayIntegrityManager(): PlayIntegrityManager {
        return playIntegrityManager
    }
}

/**
 * 完整性檢查結果
 */
data class IntegrityCheckResult(
    val isSuccess: Boolean,
    val message: String,
    val isCompatible: Boolean
)
