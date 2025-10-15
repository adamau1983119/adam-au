package com.wongtaisim.lingqian.ui.navigation

/**
 * 應用程式畫面路由定義
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CategorySelection : Screen("category_selection")
    object PrayerSteps : Screen("prayer_steps")
    object QianPreview : Screen("qian_preview")
    object CupThrowing : Screen("cup_throwing")
    object QianContent : Screen("qian_content")
    object Chat : Screen("chat")
    object BrowseQian : Screen("browse_qian")
}
