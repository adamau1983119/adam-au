package com.example.wtsaskingforsignature.ui.chatnew

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wtsaskingforsignature.data.ServiceLocator
import com.example.wtsaskingforsignature.data.api.ChatMessage
import com.example.wtsaskingforsignature.util.WtsLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChatScreenNew(nav: NavHostController, id: Int) {
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val question = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFF7ECEB), Color(0xFFF1E4E7)))
            )
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "DeepSeek 對話 — 第 ${id} 靈簽",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(8.dp))

        // Quick suggestion chips
        val suggestions = listOf("重點摘要", "吉凶解讀", "行動建議", "注意事項", "一句忠告")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.forEach { s ->
                AssistChip(onClick = { question.value = s }, label = { Text(s) })
            }
        }
        Spacer(Modifier.height(12.dp))

        // Messages list
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { m ->
                ChatBubble(role = m.role, text = m.content)
                Spacer(Modifier.height(8.dp))
            }
            if (loading.value) {
                item { TypingIndicator() }
            }
        }

        if (error.value != null) {
            Text("錯誤：${error.value}", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = question.value,
                onValueChange = { question.value = it },
                modifier = Modifier.weight(1f),
                label = { Text("輸入問題（200字內）") },
                singleLine = true,
                supportingText = { Text("${question.value.length}/200") }
            )
            Button(
                onClick = {
                    if (question.value.isBlank()) return@Button
                    loading.value = true
                    error.value = null
                    scope.launch {
                        val res = withContext(Dispatchers.IO) { ServiceLocator.repository.chat(id, question.value) }
                        res.onSuccess { resp ->
                            messages.clear()
                            messages.addAll(resp.messages)
                            question.value = ""
                        }.onFailure { e ->
                            WtsLogger.e("chat() failed: ${e.message}")
                            error.value = e.message
                        }
                        loading.value = false
                    }
                },
                enabled = !loading.value
            ) { Text(if (loading.value) "傳送中..." else "發送") }
        }

        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = { nav.navigateUp() }) { Text("返回") }
            OutlinedButton(onClick = { nav.navigate("content/${id}") }) { Text("回籤文") }
        }
    }
}

@Composable
private fun ChatBubble(role: String, text: String) {
    val isAi = role.lowercase().contains("assistant") || role.lowercase().contains("ai")
    val bg = if (isAi) Color(0xFFE1B0B6) else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (isAi) Color.White else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isAi) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Surface(
            color = bg,
            contentColor = fg,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text, modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Text("對方輸入中…", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


