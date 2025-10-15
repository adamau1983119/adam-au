package com.wongtaisim.lingqian.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wongtaisim.lingqian.data.model.QianCategory
import com.wongtaisim.lingqian.data.repository.QianRepository
import com.wongtaisim.lingqian.integrity.IntegrityVerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主畫面ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val qianRepository: QianRepository,
    private val integrityService: IntegrityVerificationService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        checkIntegrityStatus()
    }
    
    private fun checkIntegrityStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                integrityStatus = integrityService.getStatusDescription()
            )
        }
    }
    
    fun onDirectDrawClick() {
        _uiState.value = _uiState.value.copy(
            selectedDrawType = DrawType.DIRECT
        )
    }
    
    fun onCupDrawClick() {
        _uiState.value = _uiState.value.copy(
            selectedDrawType = DrawType.CUP
        )
    }
    
    fun onDailyDrawClick() {
        _uiState.value = _uiState.value.copy(
            selectedDrawType = DrawType.DAILY
        )
    }
    
    fun onBrowseQianClick() {
        _uiState.value = _uiState.value.copy(
            selectedDrawType = DrawType.BROWSE
        )
    }
}

/**
 * 主畫面UI狀態
 */
data class HomeUiState(
    val selectedDrawType: DrawType? = null,
    val integrityStatus: String = "未驗證",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 抽籤類型
 */
enum class DrawType {
    DIRECT,    // 直接抽籤
    CUP,       // 摘杯抽籤
    DAILY,     // 每日一籤
    BROWSE     // 瀏覽籤文
}
