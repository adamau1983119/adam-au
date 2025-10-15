package com.wongtaisim.lingqian.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 廣告管理器
 * 負責管理插頁式廣告的載入和顯示
 */
class AdManager(private val context: Context) {
    
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    
    companion object {
        private const val TAG = "AdManager"
        
        // 測試廣告單元ID（正式發布時需要替換為真實ID）
        private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        
        // 正式廣告單元ID（需要從AdMob Console獲取）
        private const val PRODUCTION_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
        
        // 當前使用的廣告單元ID
        private const val INTERSTITIAL_AD_UNIT_ID = TEST_INTERSTITIAL_AD_UNIT_ID
    }
    
    /**
     * 載入插頁式廣告
     */
    fun loadInterstitialAd() {
        if (isAdLoading || interstitialAd != null) {
            Log.d(TAG, "Ad already loading or loaded")
            return
        }
        
        isAdLoading = true
        Log.d(TAG, "Loading interstitial ad...")
        
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${adError.message}")
                    interstitialAd = null
                    isAdLoading = false
                }
                
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                    isAdLoading = false
                    
                    // 設置全螢幕內容回調
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad dismissed")
                            interstitialAd = null
                            // 重新載入下一個廣告
                            loadInterstitialAd()
                        }
                        
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                            interstitialAd = null
                        }
                        
                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad showed")
                        }
                    }
                }
            }
        )
    }
    
    /**
     * 顯示插頁式廣告
     * @param onAdClosed 廣告關閉後的回調
     * @return 是否成功顯示廣告
     */
    suspend fun showInterstitialAd(onAdClosed: () -> Unit = {}): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val ad = interstitialAd
            if (ad != null) {
                Log.d(TAG, "Showing interstitial ad")
                ad.show(context as android.app.Activity)
                
                // 設置回調
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Interstitial ad dismissed")
                        interstitialAd = null
                        onAdClosed()
                        continuation.resume(true)
                        // 重新載入下一個廣告
                        loadInterstitialAd()
                    }
                    
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                        interstitialAd = null
                        continuation.resume(false)
                    }
                    
                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Interstitial ad showed")
                    }
                }
            } else {
                Log.w(TAG, "Interstitial ad not ready")
                continuation.resume(false)
            }
        }
    }
    
    /**
     * 檢查廣告是否已載入
     */
    fun isAdLoaded(): Boolean {
        return interstitialAd != null
    }
    
    /**
     * 檢查廣告是否正在載入
     */
    fun isAdLoading(): Boolean {
        return isAdLoading
    }
    
    /**
     * 預載入廣告（在應用程式啟動時調用）
     */
    fun preloadAd() {
        loadInterstitialAd()
    }
}
