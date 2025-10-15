package com.wongtaisim.lingqian.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * 對話界面（DeepSeek）
 * 基於抽中之籤文與使用者問題，提供 200 字內的答覆
 */
@Composable
fun ChatScreen(navController: NavController) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    val listState = rememberLazyListState()
    
    // 廣告相關狀態
    var isShowingAd by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val application = context.applicationContext as WongTaiSimApplication
    val adManager = application.adManager
    val coroutineScope = rememberCoroutineScope()
    
    // 自動滾動到最新訊息
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // 標題列
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "對話界面",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextOnPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // 對話列表
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }
        
        // 輸入區域
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 輸入框
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { 
                        if (it.length <= 200) {
                            messageText = it
                        }
                    },
                    placeholder = { Text("在此輸入訊息...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = TextSecondary
                    )
                )
                
                // 字數統計和發送按鈕
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${messageText.length}/200",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    
                    Button(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                // 添加使用者訊息
                                val userMessage = ChatMessage(
                                    text = messageText,
                                    isUser = true,
                                    timestamp = System.currentTimeMillis()
                                )
                                messages = messages + userMessage
                                
                                // DeepSeek產生結果時觸發廣告
                                coroutineScope.launch {
                                    // 模擬AI回應
                                    val aiResponse = ChatMessage(
                                        text = generateAIResponse(messageText),
                                        isUser = false,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    messages = messages + aiResponse
                                    
                                    // 顯示廣告
                                    isShowingAd = true
                                    val adShown = adManager.showInterstitialAd {
                                        isShowingAd = false
                                    }
                                    if (!adShown) {
                                        isShowingAd = false
                                    }
                                }
                                
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGold,
                            contentColor = TextOnPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.send))
                    }
                }
            }
        }
        
        // 廣告載入狀態提示
        if (isShowingAd) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
        
        // 導航按鈕
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryGold
                )
            ) {
                Text("返回籤文")
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

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) PrimaryGold else SurfaceCream
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = if (message.isUser) "我" else "DeepSeek",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (message.isUser) TextOnPrimary else TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUser) TextOnPrimary else TextPrimary,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                )
            }
        }
    }
}

private fun generateAIResponse(userQuestion: String): String {
    // 模擬AI回應，實際應該調用DeepSeek API
    return when {
        userQuestion.contains("事業") -> "根據籤文所示，您的事業將有新的轉機。建議您保持積極態度，把握機會，必能獲得成功。"
        userQuestion.contains("感情") -> "籤文顯示感情方面將有好的發展。單身者可能遇到合適對象，有伴者關係更加穩定。"
        userQuestion.contains("財運") -> "財運方面將有改善，但需要謹慎理財，不可過度投機。穩健投資為上策。"
        userQuestion.contains("健康") -> "健康狀況良好，但要注意休息和養生。適當運動和規律作息對您很重要。"
        else -> "根據籤文指引，您所問之事將有好的結果。建議您保持信心，以正心誠意面對，必能如願以償。"
    }
}

private data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long
)
