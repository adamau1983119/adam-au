package com.example.wtsaskingforsignature

import android.app.Application
import com.example.wtsaskingforsignature.data.ServiceLocator
import com.example.wtsaskingforsignature.util.WtsLogger

class WtsApp : Application() {
	companion object {
		lateinit var instance: WtsApp
			private set
	}

	override fun onCreate() {
		super.onCreate()
		instance = this
		
		// 使用本地資料庫作為主要數據源
		ServiceLocator.dataSource = 1
		
		WtsLogger.i("WtsApp initialized, using local database")
	}
}
