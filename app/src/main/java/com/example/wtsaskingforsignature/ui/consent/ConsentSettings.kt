package com.example.wtsaskingforsignature.ui.consent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConsentSettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 標題
        Text(
            text = "隱私與同意設置",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 隱私政策說明
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "隱私政策",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "我們重視您的隱私權。本應用僅在本地處理您的個人資料，不會上傳到任何服務器。",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        
        // 數據收集說明
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "數據收集說明",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "我們收集的資料：",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Text(
                    text = "• 出生日期和時間（用於紫微斗數計算）\n• 性別（用於個性化分析）\n• 問題內容（您輸入的諮詢問題）\n• 解籤結果（生成的解籤內容）",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "我們不收集的資料：",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Text(
                    text = "• 個人身份證件信息\n• 位置信息\n• 通訊錄和聯繫人\n• 照片和媒體文件",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        
        // 數據使用說明
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "數據使用說明",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "您的資料僅用於：",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Text(
                    text = "• 生成個性化解籤分析\n• 改善應用功能\n• 提供技術支持",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        
        // 同意選項
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "同意選項",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 數據收集同意
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.dataCollectionConsent,
                        onCheckedChange = { viewModel.updateDataCollectionConsent(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "我同意應用收集上述資料用於解籤分析",
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 匿名統計同意
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.anonymousStatsConsent,
                        onCheckedChange = { viewModel.updateAnonymousStatsConsent(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "我同意應用收集匿名使用統計以改善功能",
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // 錯誤報告同意
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.errorReportingConsent,
                        onCheckedChange = { viewModel.updateErrorReportingConsent(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "我同意應用收集錯誤報告以改善穩定性",
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // 數據控制選項
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "數據控制",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 清除所有數據按鈕
                Button(
                    onClick = { viewModel.clearAllData() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("清除所有個人數據")
                }
                
                // 導出數據按鈕
                Button(
                    onClick = { viewModel.exportData() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("導出個人數據")
                }
                
                // 查看隱私政策按鈕
                OutlinedButton(
                    onClick = { viewModel.openPrivacyPolicy() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("查看完整隱私政策")
                }
            }
        }
        
        // 保存按鈕
        Button(
            onClick = { 
                viewModel.saveConsentSettings()
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = uiState.dataCollectionConsent
        ) {
            Text("保存設置")
        }
        
        // 提示信息
        if (!uiState.dataCollectionConsent) {
            Text(
                text = "請至少同意數據收集以使用解籤功能",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
