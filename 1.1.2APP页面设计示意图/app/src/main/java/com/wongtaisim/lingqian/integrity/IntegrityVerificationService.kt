package com.wongtaisim.lingqian.integrity

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 完整性驗證服務
 * 管理應用程式的完整性狀態和驗證流程
 */
class IntegrityVerificationService(private val context: Context) {
    
    private val playIntegrityManager = PlayIntegrityManager(context)
    
    private val _integrityStatus = MutableStateFlow(IntegrityStatus.UNKNOWN)
    val integrityStatus: StateFlow<IntegrityStatus> = _integrityStatus.asStateFlow()
    
    private val _lastVerificationTime = MutableStateFlow(0L)
    val lastVerificationTime: StateFlow<Long> = _lastVerificationTime.asStateFlow()
    
    companion object {
        private const val TAG = "IntegrityVerificationService"
        
        // 驗證間隔時間（毫秒）
        private const val VERIFICATION_INTERVAL = 5 * 60 * 1000L // 5分鐘
        
        // 操作類型
        const val OPERATION_DRAW_QIAN = "DRAW_QIAN"
        const val OPERATION_DAILY_QIAN = "DAILY_QIAN"
        const val OPERATION_CUP_THROWING = "CUP_THROWING"
        const val OPERATION_VIEW_QIAN = "VIEW_QIAN"
        const val OPERATION_CHAT = "CHAT"
    }
    
    /**
     * 初始化完整性驗證
     */
    suspend fun initializeIntegrity() {
        if (!playIntegrityManager.isPlayIntegrityAvailable()) {
            _integrityStatus.value = IntegrityStatus.UNAVAILABLE
            return
        }
        
        _integrityStatus.value = IntegrityStatus.VERIFYING
        
        try {
            val result = playIntegrityManager.verifyIntegrity("INITIALIZATION")
            
            when (result.status) {
                PlayIntegrityManager.INTEGRITY_SUCCESS -> {
                    _integrityStatus.value = IntegrityStatus.VERIFIED
                    _lastVerificationTime.value = System.currentTimeMillis()
                }
                PlayIntegrityManager.INTEGRITY_FAILED -> {
                    _integrityStatus.value = IntegrityStatus.FAILED
                }
                else -> {
                    _integrityStatus.value = IntegrityStatus.ERROR
                }
            }
        } catch (e: Exception) {
            _integrityStatus.value = IntegrityStatus.ERROR
        }
    }
    
    /**
     * 檢查設備是否支援Play Integrity API
     */
    fun isDeviceCompatible(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
    }
    
    /**
     * 獲取設備兼容性描述
     */
    fun getCompatibilityDescription(): String {
        return when {
            !isDeviceCompatible() -> "此設備不支援Play Integrity API（需要Android 5.0+）"
            !playIntegrityManager.isPlayIntegrityAvailable() -> "Google Play Services不可用"
            else -> "設備完全兼容"
        }
    }
    
    /**
     * 驗證特定操作
     * @param operation 操作類型
     * @return 是否通過驗證
     */
    suspend fun verifyOperation(operation: String): Boolean {
        // 檢查是否需要重新驗證
        if (shouldReverify()) {
            val result = playIntegrityManager.verifyIntegrity(operation)
            
            when (result.status) {
                PlayIntegrityManager.INTEGRITY_SUCCESS -> {
                    _integrityStatus.value = IntegrityStatus.VERIFIED
                    _lastVerificationTime.value = System.currentTimeMillis()
                    return true
                }
                PlayIntegrityManager.INTEGRITY_FAILED -> {
                    _integrityStatus.value = IntegrityStatus.FAILED
                    return false
                }
                else -> {
                    _integrityStatus.value = IntegrityStatus.ERROR
                    return false
                }
            }
        }
        
        // 如果最近已驗證過且狀態為VERIFIED，直接返回true
        return _integrityStatus.value == IntegrityStatus.VERIFIED
    }
    
    /**
     * 檢查是否需要重新驗證
     */
    private fun shouldReverify(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastTime = _lastVerificationTime.value
        
        return when {
            _integrityStatus.value == IntegrityStatus.UNKNOWN -> true
            _integrityStatus.value == IntegrityStatus.VERIFYING -> false
            _integrityStatus.value == IntegrityStatus.UNAVAILABLE -> false
            currentTime - lastTime > VERIFICATION_INTERVAL -> true
            else -> false
        }
    }
    
    /**
     * 重置驗證狀態
     */
    fun resetVerification() {
        _integrityStatus.value = IntegrityStatus.UNKNOWN
        _lastVerificationTime.value = 0L
    }
    
    /**
     * 獲取驗證狀態描述
     */
    fun getStatusDescription(): String {
        return when (_integrityStatus.value) {
            IntegrityStatus.UNKNOWN -> "未驗證"
            IntegrityStatus.VERIFYING -> "驗證中..."
            IntegrityStatus.VERIFIED -> "已驗證"
            IntegrityStatus.FAILED -> "驗證失敗"
            IntegrityStatus.ERROR -> "驗證錯誤"
            IntegrityStatus.UNAVAILABLE -> "驗證不可用"
        }
    }
    
    /**
     * 完整性狀態枚舉
     */
    enum class IntegrityStatus {
        UNKNOWN,        // 未知狀態
        VERIFYING,      // 驗證中
        VERIFIED,       // 已驗證
        FAILED,         // 驗證失敗
        ERROR,          // 驗證錯誤
        UNAVAILABLE     // 驗證不可用
    }
}
