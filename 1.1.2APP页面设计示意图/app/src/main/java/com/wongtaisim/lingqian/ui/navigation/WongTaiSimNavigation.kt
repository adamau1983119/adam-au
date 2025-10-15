package com.wongtaisim.lingqian.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wongtaisim.lingqian.ui.screens.home.HomeScreen
import com.wongtaisim.lingqian.ui.screens.category.CategorySelectionScreen
import com.wongtaisim.lingqian.ui.screens.prayer.PrayerStepsScreen
import com.wongtaisim.lingqian.ui.screens.qian.QianPreviewScreen
import com.wongtaisim.lingqian.ui.screens.cup.CupThrowingScreen
import com.wongtaisim.lingqian.ui.screens.qian.QianContentScreen
import com.wongtaisim.lingqian.ui.screens.chat.ChatScreen
import com.wongtaisim.lingqian.ui.screens.browse.BrowseQianScreen

/**
 * 黃大仙靈簽應用程式導航
 */
@Composable
fun WongTaiSimNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.CategorySelection.route) {
            CategorySelectionScreen(navController = navController)
        }
        
        composable(Screen.PrayerSteps.route) {
            PrayerStepsScreen(navController = navController)
        }
        
        composable(Screen.QianPreview.route) {
            QianPreviewScreen(navController = navController)
        }
        
        composable(Screen.CupThrowing.route) {
            CupThrowingScreen(navController = navController)
        }
        
        composable(Screen.QianContent.route) {
            QianContentScreen(navController = navController)
        }
        
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        
        composable(Screen.BrowseQian.route) {
            BrowseQianScreen(navController = navController)
        }
    }
}
