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
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatScreenNew(nav: NavHostController, id: Int) {
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val question = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }
    var dialogMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFF7ECEB), Color(0xFFF1E4E7)))
            )
            .padding(16.dp)
    ) {
        // 標題：第X靈簽：名稱（若之後能帶入 title 再替換）
        val saved = nav.currentBackStackEntry?.savedStateHandle
        val titleFromPrev = saved?.get<String>("chat_title")
        val contextFromPrev = saved?.get<String>("chat_context")
        Text(
            text = titleFromPrev ?: "第 ${id} 靈簽",
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

        // 使用者基本資料區（姓名、年齡、出生地、日期、時間）
        var name by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var birthplace by remember { mutableStateOf("") }
        var birthdate by remember { mutableStateOf("") }
        var birthtime by remember { mutableStateOf("") }
        Column(Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.weight(1f), label = { Text("姓名") })
                OutlinedTextField(value = age, onValueChange = { age = it }, modifier = Modifier.width(100.dp), label = { Text("年齡") })
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = birthplace, onValueChange = { birthplace = it }, modifier = Modifier.weight(1f), label = { Text("出生地") })
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = birthdate, onValueChange = { birthdate = it }, modifier = Modifier.weight(1f), label = { Text("出生日期 YYYY-MM-DD") })
                OutlinedTextField(value = birthtime, onValueChange = { birthtime = it }, modifier = Modifier.weight(1f), label = { Text("出生時間 HH:mm") })
            }
            // 顯示最近一次輸入的問題
            if (question.value.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text("目前問題：${question.value}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    // 驗證日期時間格式：yyyy-MM-dd 與 HH:mm，禁止使用 '/'
                    fun validDate(s: String): Boolean {
                        if (!s.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return false
                        return try {
                            val f = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            f.isLenient = false
                            f.parse(s)
                            true
                        } catch (_: Exception) { false }
                    }
                    fun validTime(s: String): Boolean {
                        if (!s.matches(Regex("\\d{2}:\\d{2}"))) return false
                        return try {
                            val f = SimpleDateFormat("HH:mm", Locale.US)
                            f.isLenient = false
                            f.parse(s)
                            true
                        } catch (_: Exception) { false }
                    }
                    val errors = mutableListOf<String>()
                    if (!validDate(birthdate)) errors.add("出生日期請用 yyyy-MM-dd（例如 1983-01-19）")
                    if (!validTime(birthtime)) errors.add("出生時間請用 HH:mm（例如 08:30）")
                    if (errors.isNotEmpty()) { dialogMsg = errors.joinToString("\n"); return@Button }

                    loading.value = true
                    error.value = null
                    scope.launch {
                        // 將歷史訊息合併成上下文，讓 DeepSeek 綜合回覆
                        val history = if (messages.isEmpty()) "" else messages.joinToString("\n") { m ->
                            val who = if (m.role.lowercase().contains("assistant")) "AI" else "我"
                            "[$who] ${m.content}"
                        }
                        val enriched = buildString {
                            if (history.isNotBlank()) {
                                append("【既有對話】\n").append(history).append("\n\n")
                            }
                            if (!contextFromPrev.isNullOrBlank()) {
                                append("【籤文依據】\n").append(contextFromPrev).append("\n\n")
                            }
                            append("【基本資料】")
                            append("\n姓名：").append(name)
                            append(" 年齡：").append(age)
                            append(" 出生地：").append(birthplace)
                            append(" 出生日期：").append(birthdate)
                            append(" 出生時間：").append(birthtime)
                            append("\n【問題】").append(question.value)
                        }
                        val res = withContext(Dispatchers.IO) { ServiceLocator.repository.chat(id, enriched) }
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

        if (dialogMsg != null) {
            AlertDialog(
                onDismissRequest = { dialogMsg = null },
                confirmButton = { TextButton(onClick = { dialogMsg = null }) { Text("確定") } },
                title = { Text("輸入格式錯誤") },
                text = { Text(dialogMsg!!) }
            )
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


