package com.example.wtsaskingforsignature.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

/**
 * 語言管理工具
 * 支援多語言切換和本地化
 */
object LanguageManager {
    
    // 支援的語言列表
    enum class Language(val code: String, val displayName: String, val locale: Locale) {
        ENGLISH("en", "English", Locale.ENGLISH),
        TRADITIONAL_CHINESE("zh-TW", "繁體中文", Locale.TAIWAN),
        SIMPLIFIED_CHINESE("zh-CN", "简体中文", Locale.CHINA),
        JAPANESE("ja", "日本語", Locale.JAPAN),
        KOREAN("ko", "한국어", Locale.KOREA)
    }
    
    /**
     * 獲取當前語言
     */
    fun getCurrentLanguage(context: Context): Language {
        val currentLocale = getCurrentLocale(context)
        return Language.values().find { it.locale == currentLocale } ?: Language.ENGLISH
    }
    
    /**
     * 獲取當前 Locale
     */
    fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }
    
    /**
     * 切換語言
     */
    fun setLanguage(context: Context, language: Language) {
        val locale = language.locale
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        // 保存語言設置
        saveLanguagePreference(context, language.code)
    }
    
    /**
     * 保存語言偏好設置
     */
    private fun saveLanguagePreference(context: Context, languageCode: String) {
        val sharedPrefs = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("selected_language", languageCode).apply()
    }
    
    /**
     * 獲取保存的語言偏好設置
     */
    fun getSavedLanguagePreference(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("selected_language", null)
    }
    
    /**
     * 應用保存的語言設置
     */
    fun applySavedLanguage(context: Context) {
        val savedLanguage = getSavedLanguagePreference(context)
        if (savedLanguage != null) {
            val language = Language.values().find { it.code == savedLanguage }
            if (language != null) {
                setLanguage(context, language)
            }
        }
    }
    
    /**
     * 獲取系統語言
     */
    fun getSystemLanguage(): Language {
        val systemLocale = Locale.getDefault()
        return Language.values().find { it.locale.language == systemLocale.language } ?: Language.ENGLISH
    }
    
    /**
     * 檢查是否支援某種語言
     */
    fun isLanguageSupported(languageCode: String): Boolean {
        return Language.values().any { it.code == languageCode }
    }
    
    /**
     * 獲取語言顯示名稱
     */
    fun getLanguageDisplayName(languageCode: String): String {
        return Language.values().find { it.code == languageCode }?.displayName ?: languageCode
    }
    
    /**
     * 獲取所有支援的語言
     */
    fun getAllSupportedLanguages(): List<Language> {
        return Language.values().toList()
    }
    
    /**
     * 根據語言代碼獲取 Language 對象
     */
    fun getLanguageByCode(code: String): Language? {
        return Language.values().find { it.code == code }
    }
    
    /**
     * 創建本地化資源
     */
    fun createLocalizedResources(context: Context, language: Language): Resources {
        val locale = language.locale
        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        return context.createConfigurationContext(config).resources
    }
}
