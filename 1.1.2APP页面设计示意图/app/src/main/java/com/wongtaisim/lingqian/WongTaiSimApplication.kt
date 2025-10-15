package com.wongtaisim.lingqian

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.wongtaisim.lingqian.ads.AdManager
import com.wongtaisim.lingqian.integrity.IntegrityVerificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 黃大仙靈簽應用程式類別
 * 負責初始化應用程式和服務
 */
class WongTaiSimApplication : Application() {
    
    // 應用程式範圍的協程作用域
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // 完整性驗證服務
    lateinit var integrityService: IntegrityVerificationService
        private set
    
    // 廣告管理器
    lateinit var adManager: AdManager
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化Google Mobile Ads SDK
        MobileAds.initialize(this) { initializationStatus ->
            // 廣告SDK初始化完成
        }
        
        // 初始化廣告管理器
        adManager = AdManager(this)
        
        // 初始化完整性驗證服務
        integrityService = IntegrityVerificationService(this)
        
        // 在背景初始化服務
        applicationScope.launch {
            integrityService.initializeIntegrity()
            // 預載入廣告
            adManager.preloadAd()
        }
    }
}
