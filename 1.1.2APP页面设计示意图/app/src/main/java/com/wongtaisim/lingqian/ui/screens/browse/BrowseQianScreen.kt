package com.wongtaisim.lingqian.ui.screens.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * 瀏覽籤文畫面
 * 瀏覽1~100號所有籤文（非抽籤）
 */
@Composable
fun BrowseQianScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // 廣告相關狀態
    var isShowingAd by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val application = context.applicationContext as WongTaiSimApplication
    val adManager = application.adManager
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // 標題
        Text(
            text = stringResource(R.string.browse_qian),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        
        // 篩選區域
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "篩選：",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // 類別篩選按鈕
                LazyColumn(
                    modifier = Modifier.height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories.chunked(5)) { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCategories.forEach { category ->
                                FilterChip(
                                    onClick = { 
                                        selectedCategory = if (selectedCategory == category.id) null else category.id
                                    },
                                    label = { Text(category.displayName) },
                                    selected = selectedCategory == category.id,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGold,
                                        selectedLabelColor = TextOnPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 籤文清單
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceCream),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "清單（1~100）：",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(qianList.filter { 
                        selectedCategory == null || it.category == selectedCategory 
                    }) { qian ->
                        QianListItem(
                            qian = qian,
                            onClick = { 
                                // 點擊查看籤文時觸發廣告
                                coroutineScope.launch {
                                    isShowingAd = true
                                    val adShown = adManager.showInterstitialAd {
                                        isShowingAd = false
                                    }
                                    if (!adShown) {
                                        isShowingAd = false
                                    }
                                    // 廣告關閉後跳轉到籤文內容
                                    navController.navigate(Screen.QianContent.route)
                                }
                            }
                        )
                    }
                }
            }
        }
        
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
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 返回按鈕
        Button(
            onClick = { navController.navigate(Screen.Home.route) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGold,
                contentColor = TextOnPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.return_home),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun QianListItem(
    qian: QianItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryGoldLight),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "#${qian.number} ${qian.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Text(
                    text = qian.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2
                )
            }
            
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGold,
                    contentColor = TextOnPrimary
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("查看")
            }
        }
    }
}

// 籤文項目資料類別
private data class QianItem(
    val number: Int,
    val title: String,
    val summary: String,
    val category: String
)

// 籤文清單資料
private val qianList = (1..100).map { number ->
    QianItem(
        number = number,
        title = "第${number}籤",
        summary = "此籤顯示所求之事將有轉機，需要耐心等待時機成熟。",
        category = categories.random().id
    )
}

// 篩選類別
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

private data class Category(
    val id: String,
    val displayName: String
)
