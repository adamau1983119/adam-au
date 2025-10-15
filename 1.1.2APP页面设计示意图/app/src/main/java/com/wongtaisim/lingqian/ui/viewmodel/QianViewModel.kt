package com.wongtaisim.lingqian.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wongtaisim.lingqian.data.model.*
import com.wongtaisim.lingqian.data.repository.QianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 籤文相關ViewModel
 */
@HiltViewModel
class QianViewModel @Inject constructor(
    private val qianRepository: QianRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QianUiState())
    val uiState: StateFlow<QianUiState> = _uiState.asStateFlow()
    
    fun drawQian(category: QianCategory, isDaily: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = if (isDaily) {
                    qianRepository.drawDailyQian(category)
                } else {
                    qianRepository.drawQian(category)
                }
                
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            drawQianResponse = response,
                            currentQianData = response.qianData
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "抽籤失敗"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "發生未知錯誤"
                )
            }
        }
    }
    
    fun validateCupThrowing(results: List<CupResult>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = qianRepository.validateCupThrowing(results)
                
                result.fold(
                    onSuccess = { validationResult ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            cupValidationResult = validationResult
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "擲杯驗證失敗"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "發生未知錯誤"
                )
            }
        }
    }
    
    fun chatWithDeepSeek(question: String) {
        val currentQianData = _uiState.value.currentQianData
        if (currentQianData == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "請先抽籤再進行對話"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = qianRepository.chatWithDeepSeek(question, currentQianData)
                
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            chatResponse = response
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "對話失敗"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "發生未知錯誤"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetState() {
        _uiState.value = QianUiState()
    }
}

/**
 * 籤文UI狀態
 */
data class QianUiState(
    val isLoading: Boolean = false,
    val currentQianData: QianData? = null,
    val drawQianResponse: DrawQianResponse? = null,
    val cupValidationResult: CupValidationResult? = null,
    val chatResponse: ChatResponse? = null,
    val errorMessage: String? = null
)
