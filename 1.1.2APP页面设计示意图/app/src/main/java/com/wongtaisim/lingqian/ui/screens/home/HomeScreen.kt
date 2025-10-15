package com.wongtaisim.lingqian.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wongtaisim.lingqian.R
import com.wongtaisim.lingqian.ui.navigation.Screen
import com.wongtaisim.lingqian.ui.theme.*
import androidx.compose.foundation.layout.statusBarsPadding

/**
 * 主畫面（首頁）
 * 提供四個主要功能入口
 */
@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 標題
        Text(
            text = stringResource(R.string.main_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // App Logo 佔位符
        Card(
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGoldLight)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "黃大仙",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextOnPrimary
                )
            }
        }
        
        // 功能按鈕
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 直接抽籤
            HomeButton(
                text = stringResource(R.string.direct_draw),
                onClick = { navController.navigate(Screen.CategorySelection.route) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 摘杯抽籤
            HomeButton(
                text = stringResource(R.string.cup_draw),
                onClick = { navController.navigate(Screen.CategorySelection.route) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 每日一籤
            HomeButton(
                text = stringResource(R.string.daily_draw),
                onClick = { navController.navigate(Screen.CategorySelection.route) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 瀏覽籤文
            HomeButton(
                text = stringResource(R.string.browse_qian),
                onClick = { navController.navigate(Screen.BrowseQian.route) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 說明文字
        Text(
            text = "說明：點擊任一功能進入對應頁面",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HomeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGold,
            contentColor = TextOnPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
