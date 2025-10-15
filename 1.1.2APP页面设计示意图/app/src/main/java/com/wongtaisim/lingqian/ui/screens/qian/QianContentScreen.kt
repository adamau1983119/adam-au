package com.wongtaisim.lingqian.ui.screens.qian

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
 * 籤文內容畫面
 * 顯示對應的籤文全文，並提供與 DeepSeek 的對話入口
 */
@Composable
fun QianContentScreen(navController: NavController) {
    var questionText by remember { mutableStateOf("") }
    val qianNumber = 42 // 模擬籤號
    
    // 廣告相關狀態
    var isShowingAd by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val application = context.applicationContext as WongTaiSimApplication
    val adManager = application.adManager
    val coroutineScope = rememberCoroutineScope()
    
    // 進入籤文內容頁面時觸發廣告
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500) // 延遲500ms後顯示廣告
        isShowingAd = true
        val adShown = adManager.showInterstitialAd {
            isShowingAd = false
        }
        if (!adShown) {
            isShowingAd = false
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 標題
        Text(
            text = stringResource(R.string.qian_number, qianNumber) + " 上上籤",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        // 籤文內容卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "籤文內容：",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "此籤乃上上籤，主大吉大利。所求之事，必能如願以償。但須謹記，成功之道在於誠心正意，不可心存僥倖。\n\n" +
                            "事業方面，將有貴人相助，機會來臨時要把握。財運亨通，但不可貪心，適可而止。感情方面，有情人終成眷屬，單身者將遇良緣。\n\n" +
                            "健康方面，身體康健，但要注意休息，不可過度勞累。家庭和睦，長輩安康，子女孝順。\n\n" +
                            "總而言之，此籤顯示一切順遂，但需要以正心誠意為本，方能長久。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 問答區域
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PrimaryGoldLight),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "問答（DeepSeek）：",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 問題輸入框
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { 
                        if (it.length <= 200) {
                            questionText = it
                        }
                    },
                    placeholder = { Text(stringResource(R.string.ask_question)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = TextSecondary
                    )
                )
                
                // 字數統計
                Text(
                    text = "${questionText.length}/200",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.End
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 發送按鈕
                Button(
                    onClick = { 
                        if (questionText.isNotBlank()) {
                            // 點擊DeepSeek解簽按鈕時觸發廣告
                            coroutineScope.launch {
                                isShowingAd = true
                                val adShown = adManager.showInterstitialAd {
                                    isShowingAd = false
                                }
                                if (!adShown) {
                                    isShowingAd = false
                                }
                                // 廣告關閉後跳轉到對話界面
                                navController.navigate(Screen.Chat.route)
                            }
                        }
                    },
                    enabled = questionText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGold,
                        contentColor = TextOnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.send),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.conversation_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 廣告載入狀態提示
        if (isShowingAd) {
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
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 導航按鈕
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryGold
                )
            ) {
                Text(stringResource(R.string.back))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Button(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGold,
                    contentColor = TextOnPrimary
                )
            ) {
                Text(stringResource(R.string.return_home))
            }
        }
    }
}
