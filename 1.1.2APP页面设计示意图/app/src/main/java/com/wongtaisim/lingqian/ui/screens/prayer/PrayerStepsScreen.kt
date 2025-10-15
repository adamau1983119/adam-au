package com.wongtaisim.lingqian.ui.screens.prayer

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
 * 求籤步驟畫面
 * 引導使用者按傳統流程完成求籤儀式並開始抽籤
 */
@Composable
fun PrayerStepsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 標題
        Text(
            text = stringResource(R.string.prayer_steps),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        // 步驟卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 步驟1
                PrayerStep(
                    stepNumber = 1,
                    description = stringResource(R.string.prayer_step_1)
                )
                
                // 步驟2
                PrayerStep(
                    stepNumber = 2,
                    description = stringResource(R.string.prayer_step_2)
                )
                
                // 步驟3
                PrayerStep(
                    stepNumber = 3,
                    description = stringResource(R.string.prayer_step_3)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 開始抽籤按鈕
        Button(
            onClick = { navController.navigate(Screen.QianPreview.route) },
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
                text = stringResource(R.string.start_draw),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
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
                Text(stringResource(R.string.previous))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            OutlinedButton(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryGold
                )
            ) {
                Text(stringResource(R.string.back))
            }
        }
    }
}

@Composable
private fun PrayerStep(
    stepNumber: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // 步驟編號
        Card(
            modifier = Modifier.size(32.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGold),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextOnPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 步驟描述
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}
