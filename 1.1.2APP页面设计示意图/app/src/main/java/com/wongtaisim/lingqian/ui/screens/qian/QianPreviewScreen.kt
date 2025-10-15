package com.wongtaisim.lingqian.ui.screens.qian

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wongtaisim.lingqian.R
import com.wongtaisim.lingqian.ui.navigation.Screen
import com.wongtaisim.lingqian.ui.theme.*
import androidx.compose.foundation.layout.statusBarsPadding

/**
 * 簽文預覽畫面（含擲杯入口）
 * 顯示抽中的第幾籤，並提供擲杯入口以驗證籤意
 */
@Composable
fun QianPreviewScreen(navController: NavController) {
    // 模擬抽中的籤號
    val qianNumber by remember { mutableStateOf((1..100).random()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 標題
        Text(
            text = stringResource(R.string.you_drew_qian, stringResource(R.string.qian_number, qianNumber)),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        // 籤文摘要卡片
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
                    text = stringResource(R.string.qian_summary),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = "此籤顯示您所求之事將有轉機，但需要耐心等待時機成熟。黃大仙指示，只要心誠則靈，必能獲得指引。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
                
                Text(
                    text = "（完整內容稍後在籤文內容頁顯示）",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 擲杯驗證區域
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
                Text(
                    text = "擲杯驗證（需完成三次）：",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 擲杯按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CupButton(
                        text = "擲杯 1",
                        onClick = { navController.navigate(Screen.CupThrowing.route) }
                    )
                    CupButton(
                        text = "擲杯 2",
                        onClick = { navController.navigate(Screen.CupThrowing.route) }
                    )
                    CupButton(
                        text = "擲杯 3",
                        onClick = { navController.navigate(Screen.CupThrowing.route) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 其他選項
        OutlinedButton(
            onClick = { navController.navigate(Screen.QianContent.route) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryGold
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.view_full_qian),
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
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
                onClick = { navController.navigate(Screen.QianContent.route) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGold,
                    contentColor = TextOnPrimary
                )
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}

@Composable
private fun CupButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(80.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGold,
            contentColor = TextOnPrimary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
