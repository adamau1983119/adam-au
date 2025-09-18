package com.example.wtsaskingforsignature.data

object ServiceLocator {
	private val localRepository: LocalRepository by lazy { LocalRepository() }
	private val localAIRepository: LocalAIRepository by lazy { LocalAIRepository() }

	// 切換資料來源：0=使用本地AI，1=使用本地資料庫
	var dataSource: Int = 1 // 預設使用本地資料庫

	val repository: Repository
		get() = when (dataSource) {
			0 -> localAIRepository      // 本地AI（备用）
			1 -> localRepository        // 本地資料庫（主要）
			else -> localRepository     // 預設使用本地資料庫
		}
}
