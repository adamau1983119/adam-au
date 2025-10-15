package com.wongtaisim.lingqian.ui.screens.cup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wongtaisim.lingqian.R
import com.wongtaisim.lingqian.WongTaiSimApplication
import com.wongtaisim.lingqian.ui.navigation.Screen
import com.wongtaisim.lingqian.ui.theme.*
import androidx.compose.foundation.layout.statusBarsPadding
import kotlinx.coroutines.launch

/**
 * 擲杯畫面
 * 引導完成三次擲杯並顯示結果，判斷是否可進入籤文內容頁
 */
@Composable
fun CupThrowingScreen(navController: NavController) {
    var currentThrow by remember { mutableStateOf(1) }
    var throwResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var isCompleted by remember { mutableStateOf(false) }
    var isShowingAd by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val application = context.applicationContext as WongTaiSimApplication
    val adManager = application.adManager
    val coroutineScope = rememberCoroutineScope()
    
    // 計算結果
    val positiveCount = throwResults.count { it == "正" }
    val negativeCount = throwResults.count { it == "反" }
    val canProceed = positiveCount >= 2 || negativeCount >= 2
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 標題
        Text(
            text = "擲杯",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        // 說明卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.cup_throwing_instruction),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 進度和結果顯示
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PrimaryGoldLight),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 次數進度
                Text(
                    text = "次數：${currentThrow} / 3",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 本次結果
                if (throwResults.isNotEmpty()) {
                    Text(
                        text = "本次結果：${throwResults.last()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // 累計結果
                if (throwResults.isNotEmpty()) {
                    Text(
                        text = "累計結果：正×$positiveCount 反×$negativeCount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 擲杯按鈕
        Button(
            onClick = {
                if (currentThrow <= 3) {
                    // 模擬擲杯結果
                    val result = if ((0..1).random() == 0) "正" else "反"
                    throwResults = throwResults + result
                    
                    if (currentThrow == 3) {
                        isCompleted = true
                        // 檢查是否達到成功條件，如果達到則自動跳轉
                        val newPositiveCount = throwResults.count { it == "正" }
                        val newNegativeCount = throwResults.count { it == "反" }
                        val canProceedNow = newPositiveCount >= 2 || newNegativeCount >= 2
                        
                        // 不自動跳轉，等待用戶點擊「查看籤文內容」按鈕
                    } else {
                        currentThrow++
                    }
                }
            },
            enabled = !isCompleted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGold,
                contentColor = TextOnPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isCompleted) "已完成" else if (currentThrow <= 3) "擲杯" else "下一次",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 完成條件說明
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (canProceed) PrimaryGoldLight else TertiaryOrangeLight
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.cup_complete_condition),
                style = MaterialTheme.typography.bodyMedium,
                color = if (canProceed) TextPrimary else TertiaryOrange,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // 擲杯完成後的結果顯示和按鈕
        if (isCompleted) {
            Spacer(modifier = Modifier.height(24.dp))
            
            val positiveCount = throwResults.count { it == "正" }
            val negativeCount = throwResults.count { it == "反" }
            val canProceed = positiveCount >= 2 || negativeCount >= 2
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryGoldLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "擲杯結果",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 顯示三次擲杯結果
                    Text(
                        text = "三次擲杯紀錄：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    throwResults.forEachIndexed { index, result ->
                        Text(
                            text = "第${index + 1}次：$result",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 系統判定
                    Text(
                        text = "系統判定：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (canProceed) {
                        Text(
                            text = "✓ 可視為生效",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "✗ 未生效，請重新抽籤",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF44336),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 按鈕區域
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 重新抽籤按鈕
                        Button(
                            onClick = {
                                // 重置狀態
                                currentThrow = 1
                                throwResults = emptyList()
                                isCompleted = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "重新抽籤",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        // 查看籤文內容按鈕（只有達到條件時才啟用）
                        Button(
                            onClick = {
                                if (canProceed) {
                                    // 顯示廣告後跳轉到籤文內容
                                    coroutineScope.launch {
                                        isShowingAd = true
                                        val adShown = adManager.showInterstitialAd {
                                            // 廣告關閉後的回調
                                            isShowingAd = false
                                        }
                                        
                                        // 如果廣告沒有顯示，直接跳轉
                                        if (!adShown) {
                                            isShowingAd = false
                                        }
                                        
                                        // 跳轉到籤文內容（無論廣告是否顯示）
                                        navController.navigate(Screen.QianContent.route)
                                    }
                                }
                            },
                            enabled = canProceed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canProceed) PrimaryGold else Color(0xFFCCCCCC)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "查看籤文內容",
                                color = if (canProceed) Color.White else Color(0xFF999999),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        // 廣告載入狀態提示
        if (isShowingAd) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryGoldLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PrimaryGold,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "正在載入廣告...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 動作按鈕
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { 
                    // 重置擲杯狀態
                    currentThrow = 1
                    throwResults = emptyList()
                    isCompleted = false
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryGold
                )
            ) {
                Text("重新抽籤")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Button(
                onClick = { navController.navigate(Screen.QianContent.route) },
                enabled = isCompleted && canProceed,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGold,
                    contentColor = TextOnPrimary
                )
            ) {
                Text("完成並查看籤文")
            }
        }
    }
}
