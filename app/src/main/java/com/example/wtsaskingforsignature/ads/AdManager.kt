package com.example.wtsaskingforsignature.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 廣告管理器
 * 負責載入和顯示插頁式廣告
 */
class AdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private val _isAdLoading = MutableStateFlow(false)
    val isAdLoading: StateFlow<Boolean> = _isAdLoading.asStateFlow()

    companion object {
        private const val TAG = "AdManager"
    }

    /**
     * 預載入插頁式廣告
     */
    fun preloadAd() {
        if (interstitialAd != null || _isAdLoading.value) {
            Log.d(TAG, "Ad already loaded or loading.")
            return
        }

        _isAdLoading.value = true
        val adRequest = AdRequest.Builder().build()
        val adUnitId = "ca-app-pub-3940256099942544/1033173712" // 測試廣告單元ID

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Interstitial ad failed to load: ${adError.message}")
                interstitialAd = null
                _isAdLoading.value = false
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d(TAG, "Interstitial ad loaded.")
                interstitialAd = ad
                _isAdLoading.value = false
            }
        })
    }

    /**
     * 顯示插頁式廣告
     * @param onAdDismissed 廣告關閉後的回調
     * @return 如果廣告成功顯示則返回 true，否則返回 false
     */
    fun showInterstitialAd(onAdDismissed: () -> Unit): Boolean {
        val activity = context as? android.app.Activity
        if (activity == null) {
            Log.e(TAG, "Context is not an Activity, cannot show ad.")
            onAdDismissed()
            return false
        }

        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    interstitialAd = null
                    preloadAd() // 廣告關閉後預載入下一個廣告
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Ad failed to show: ${adError.message}")
                    interstitialAd = null
                    preloadAd() // 廣告顯示失敗後預載入下一個廣告
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed on full screen content.")
                }
            }
            interstitialAd?.show(activity)
            return true
        } else {
            Log.d(TAG, "Interstitial ad wasn't ready yet.")
            preloadAd() // 嘗試重新載入廣告
            onAdDismissed()
            return false
        }
    }
}
