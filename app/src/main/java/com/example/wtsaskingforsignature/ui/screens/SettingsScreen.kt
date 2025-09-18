package com.example.wtsaskingforsignature.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.wtsaskingforsignature.util.LanguageManager

/**
 * 設置界面
 */
@Composable
fun SettingsScreen(nav: NavHostController) {
    val context = LocalContext.current
    val currentLanguage = LanguageManager.getCurrentLanguage(context)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // 標題
            Text(
                text = "設置",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        
        item {
            // 語言設置卡片
            SettingsCard(
                title = "語言設置",
                subtitle = "當前語言：${currentLanguage.displayName}",
                icon = Icons.Default.Settings,
                onClick = {
                    // 導航到語言設置界面
                    nav.navigate("language_settings")
                }
            )
        }
        
        item {
            // 關於應用卡片
            SettingsCard(
                title = "關於應用",
                subtitle = "版本信息、隱私政策等",
                icon = Icons.Default.Info,
                onClick = {
                    // 導航到關於界面
                    nav.navigate("about")
                }
            )
        }
        
        item {
            // 幫助與支持卡片
            SettingsCard(
                title = "幫助與支持",
                subtitle = "使用說明、常見問題",
                icon = Icons.Default.Info,
                onClick = {
                    // 導航到幫助界面
                    nav.navigate("help")
                }
            )
        }
        
        item {
            // 數據管理卡片
            SettingsCard(
                title = "數據管理",
                subtitle = "清除緩存、重置設置",
                icon = Icons.Default.Settings,
                onClick = {
                    // 導航到數據管理界面
                    nav.navigate("data_management")
                }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
            
            // 當前語言信息
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "當前語言信息",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "語言代碼：${currentLanguage.code}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "語言名稱：${currentLanguage.displayName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "系統語言：${LanguageManager.getSystemLanguage().displayName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * 設置選項卡片
 */
@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 圖標
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // 文字內容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 箭頭圖標
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
