package com.example.wtsaskingforsignature.util

import android.util.Log

object WtsLogger {
	private const val TAG: String = "WTS"
	private const val PREFIX: String = "[WTS] "

	fun d(message: String) {
		Log.d(TAG, PREFIX + message)
	}

	fun i(message: String) {
		Log.i(TAG, PREFIX + message)
	}

	fun w(message: String, tr: Throwable? = null) {
		if (tr != null) Log.w(TAG, PREFIX + message, tr) else Log.w(TAG, PREFIX + message)
	}

	fun e(message: String, tr: Throwable? = null) {
		if (tr != null) Log.e(TAG, PREFIX + message, tr) else Log.e(TAG, PREFIX + message)
	}
}
