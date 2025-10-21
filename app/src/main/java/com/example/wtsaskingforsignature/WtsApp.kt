package com.example.wtsaskingforsignature

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.example.wtsaskingforsignature.ads.AdManager
import com.example.wtsaskingforsignature.integrity.IntegrityVerificationService
import com.example.wtsaskingforsignature.data.ServiceLocator
import com.example.wtsaskingforsignature.util.WtsLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.wtsaskingforsignature.BuildConfig

class WtsApp : Application() {
	companion object {
		lateinit var instance: WtsApp
			private set
	}

	// 廣告管理器
	lateinit var adManager: AdManager
		private set
	
	// 完整性驗證服務
	lateinit var integrityService: IntegrityVerificationService
		private set

	// 應用程式範圍的協程作用域
	private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

	override fun onCreate() {
		super.onCreate()
		instance = this
		
		// 初始化Google Mobile Ads SDK
		MobileAds.initialize(this) { initializationStatus ->
			WtsLogger.i("AdMob SDK initialized")
		}
		
		// 初始化廣告管理器
		adManager = AdManager(this)
		
		// 初始化完整性驗證服務
		integrityService = IntegrityVerificationService(this)
		
        // 使用資料源：0=LocalAIRepository(DeepSeekInterpreter)，1=LocalRepository(傳統模板)
        ServiceLocator.dataSource = if (BuildConfig.BUILD_TYPE == "debug") 0 else 1
        WtsLogger.i("ServiceLocator.dataSource=${ServiceLocator.dataSource} (0=AI,1=DB)")
		
		// 在背景初始化服務
		applicationScope.launch {
			integrityService.initializeIntegrity()
			// 預載入廣告
			adManager.preloadAd()
		}
		
		WtsLogger.i("WtsApp initialized, using local database")
	}
}
