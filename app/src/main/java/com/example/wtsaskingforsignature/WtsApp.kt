package com.example.wtsaskingforsignature

import android.app.Application
import com.example.wtsaskingforsignature.data.ServiceLocator

class WtsApp : Application() {
	override fun onCreate() {
		super.onCreate()
		instance = this
		// 強制使用本地離線資料（讀取 assets/fortunes.json）
		ServiceLocator.useRemote = false
	}

	companion object {
		lateinit var instance: WtsApp
			private set
	}
}
