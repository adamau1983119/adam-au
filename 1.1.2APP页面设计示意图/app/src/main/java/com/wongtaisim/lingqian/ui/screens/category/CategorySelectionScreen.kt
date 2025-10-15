package com.wongtaisim.lingqian.ui.screens.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * 選擇占卜類別畫面
 * 適用於：直接抽籤、摘杯抽籤、每日一籤
 */
@Composable
fun CategorySelectionScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // 標題
        Text(
            text = stringResource(R.string.category_selection),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        Text(
            text = "請選擇所求之事：",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 類別網格
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categories) { category ->
                CategoryButton(
                    text = category.displayName,
                    isSelected = selectedCategory == category.id,
                    onClick = { selectedCategory = category.id }
                )
            }
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
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = null
                )
            ) {
                Text(stringResource(R.string.back))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Button(
                onClick = { 
                    if (selectedCategory != null) {
                        navController.navigate(Screen.PrayerSteps.route)
                    }
                },
                enabled = selectedCategory != null,
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
private fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGold else SurfaceCream
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) TextOnPrimary else TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// 占卜類別資料
private data class Category(
    val id: String,
    val displayName: String
)

private val categories = listOf(
    Category("career", "事業"),
    Category("wealth", "財運"),
    Category("self", "自身"),
    Category("family", "家庭"),
    Category("marriage", "婚姻"),
    Category("migration", "遷徙"),
    Category("reputation", "名譽"),
    Category("health", "健康"),
    Category("friendship", "友誼"),
    Category("other", "其他")
)
