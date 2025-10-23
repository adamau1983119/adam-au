package com.example.wtsaskingforsignature.ui.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConsentSettingsUiState(
    val dataCollectionConsent: Boolean = false,
    val anonymousStatsConsent: Boolean = false,
    val errorReportingConsent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ConsentSettingsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ConsentSettingsUiState())
    val uiState: StateFlow<ConsentSettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadConsentSettings()
    }
    
    private fun loadConsentSettings() {
        viewModelScope.launch {
            try {
                // 從SharedPreferences或數據庫加載設置
                val dataCollection = getStoredConsent("data_collection", false)
                val anonymousStats = getStoredConsent("anonymous_stats", true)
                val errorReporting = getStoredConsent("error_reporting", true)
                
                _uiState.value = _uiState.value.copy(
                    dataCollectionConsent = dataCollection,
                    anonymousStatsConsent = anonymousStats,
                    errorReportingConsent = errorReporting,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "加載設置失敗: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun updateDataCollectionConsent(consent: Boolean) {
        _uiState.value = _uiState.value.copy(
            dataCollectionConsent = consent
        )
    }
    
    fun updateAnonymousStatsConsent(consent: Boolean) {
        _uiState.value = _uiState.value.copy(
            anonymousStatsConsent = consent
        )
    }
    
    fun updateErrorReportingConsent(consent: Boolean) {
        _uiState.value = _uiState.value.copy(
            errorReportingConsent = consent
        )
    }
    
    fun saveConsentSettings() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 保存設置到SharedPreferences
                saveConsent("data_collection", _uiState.value.dataCollectionConsent)
                saveConsent("anonymous_stats", _uiState.value.anonymousStatsConsent)
                saveConsent("error_reporting", _uiState.value.errorReportingConsent)
                
                // 記錄同意狀態變更
                logConsentChange()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "保存設置失敗: ${e.message}"
                )
            }
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 清除所有個人數據
                clearUserData()
                
                // 重置同意設置
                _uiState.value = _uiState.value.copy(
                    dataCollectionConsent = false,
                    anonymousStatsConsent = false,
                    errorReportingConsent = false,
                    isLoading = false,
                    errorMessage = "所有個人數據已清除"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "清除數據失敗: ${e.message}"
                )
            }
        }
    }
    
    fun exportData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 導出個人數據
                val exportedData = exportUserData()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "數據已導出到: $exportedData"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "導出數據失敗: ${e.message}"
                )
            }
        }
    }
    
    fun openPrivacyPolicy() {
        viewModelScope.launch {
            try {
                // 打開隱私政策頁面或外部鏈接
                openPrivacyPolicyPage()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "無法打開隱私政策: ${e.message}"
                )
            }
        }
    }
    
    private fun getStoredConsent(key: String, defaultValue: Boolean): Boolean {
        // 從SharedPreferences獲取存儲的同意狀態
        // 這裡需要實際的SharedPreferences實現
        return defaultValue
    }
    
    private fun saveConsent(key: String, value: Boolean) {
        // 保存同意狀態到SharedPreferences
        // 這裡需要實際的SharedPreferences實現
    }
    
    private fun logConsentChange() {
        // 記錄同意狀態變更日誌
        // 用於審計和合規性
    }
    
    private fun clearUserData() {
        // 清除所有用戶個人數據
        // 包括出生資料、問題記錄、解籤結果等
    }
    
    private fun exportUserData(): String {
        // 導出用戶個人數據
        // 返回導出文件路徑
        return "導出文件路徑"
    }
    
    private fun openPrivacyPolicyPage() {
        // 打開隱私政策頁面
        // 可以是應用內頁面或外部鏈接
    }
}
