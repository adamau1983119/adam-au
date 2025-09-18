package com.example.wtsaskingforsignature.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.wtsaskingforsignature.util.LanguageManager
import com.example.wtsaskingforsignature.util.LanguageManager.Language

/**
 * 語言設置界面
 */
@Composable
fun LanguageSettingsScreen(nav: NavHostController) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(LanguageManager.getCurrentLanguage(context)) }
    var showRestartDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 標題
        Text(
            text = "語言設置",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 語言選項列表
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LanguageManager.getAllSupportedLanguages()) { language ->
                LanguageOptionItem(
                    language = language,
                    isSelected = language == selectedLanguage,
                    onSelect = { 
                        selectedLanguage = language
                        showRestartDialog = true
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 說明文字
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
                    text = "語言設置說明",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "選擇您偏好的語言，應用將自動切換到對應語言。某些語言可能需要重啟應用才能完全生效。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    
    // 重啟確認對話框
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text("切換語言") },
            text = { 
                Text("語言已切換為 ${selectedLanguage.displayName}。建議重啟應用以確保所有內容都正確顯示。") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 應用語言設置
                        LanguageManager.setLanguage(context, selectedLanguage)
                        showRestartDialog = false
                        // 返回上一頁
                        nav.popBackStack()
                    }
                ) {
                    Text("確定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRestartDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 語言選項項目
 */
@Composable
private fun LanguageOptionItem(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 語言名稱
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = language.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = language.code.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 選擇指示器
            if (isSelected) {
                RadioButton(
                    selected = true,
                    onClick = onSelect
                )
            } else {
                RadioButton(
                    selected = false,
                    onClick = onSelect
                )
            }
        }
    }
}
