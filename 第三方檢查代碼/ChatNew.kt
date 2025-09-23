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
import com.example.wtsaskingforsignature.ui.theme.MdGradientBottom
import com.example.wtsaskingforsignature.ui.theme.MdGradientTop
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
import androidx.compose.foundation.clickable
import com.example.wtsaskingforsignature.ui.components.GlassCard
import com.example.wtsaskingforsignature.ui.components.WtsPrimaryButton
import com.example.wtsaskingforsignature.ui.components.WtsOutlinedTextField
import com.example.wtsaskingforsignature.ui.components.WtsWhiteButton
import com.example.wtsaskingforsignature.ui.components.WtsFrostedChoiceButton

@Composable
fun ChatScreenNew(nav: NavHostController, id: Int) {
    val scope = rememberCoroutineScope()
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val question = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<String?>(null) }
    var dialogMsg by remember { mutableStateOf<String?>(null) }
    
    // 錯誤處理：捕獲任何未處理的異常
    LaunchedEffect(Unit) {
        try {
            WtsLogger.i("ChatScreenNew initialized for fortune id: $id")
        } catch (e: Exception) {
            WtsLogger.e("ChatScreenNew initialization error: ${e.message}", e)
            error.value = "初始化失敗：${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        // 籤文依據（可展開）
        if (!contextFromPrev.isNullOrBlank()) {
            var expanded by remember { mutableStateOf(false) }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth().clickable { expanded = !expanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("籤文依據（${if (expanded) "收起" else "可展開"}）", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(6.dp))
                    val preview = if (expanded) contextFromPrev else contextFromPrev.lines().take(3).joinToString("\n")
                    Text(preview, style = MaterialTheme.typography.bodySmall)
                    if (!expanded && contextFromPrev.lines().size > 3) {
                        Spacer(Modifier.height(4.dp))
                        Text("…（點擊展開全文）", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Quick suggestion chips
        val suggestions = listOf("重點摘要", "吉凶解讀", "行動建議", "注意事項", "一句忠告", "紫微斗數綜合")
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
        var birthPeriod by remember { mutableStateOf<String?>(null) }
        var skipPersonalInfo by remember { mutableStateOf(false) }
        
        Column(Modifier.fillMaxWidth()) {
            // 添加跳過個人資料的按鈕
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                WtsWhiteButton(
                    text = if (skipPersonalInfo) "填寫個人資料" else "直接提問Deepseek",
                    onClick = { skipPersonalInfo = !skipPersonalInfo },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            if (!skipPersonalInfo) {
                // 第一行：姓名 + 年齡（統一高度）
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WtsOutlinedTextField(
                        value = name, 
                        onValueChange = { name = it }, 
                        modifier = Modifier.weight(2f), 
                        label = { Text("姓名") }
                    )
                    WtsOutlinedTextField(
                        value = age, 
                        onValueChange = { age = it }, 
                        modifier = Modifier.weight(1f), 
                        label = { Text("年齡") }
                    )
                }
                Spacer(Modifier.height(12.dp))
                
                // 第二行：出生地（單獨一行，保持一致性）
                WtsOutlinedTextField(
                    value = birthplace, 
                    onValueChange = { birthplace = it }, 
                    modifier = Modifier.fillMaxWidth(), 
                    label = { Text("出生地") }
                )
                Spacer(Modifier.height(12.dp))
                
                // 第三行：出生日期 + 出生時間（統一高度）
                var isTimeFieldFocused by remember { mutableStateOf(false) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WtsOutlinedTextField(
                        value = birthdate, 
                        onValueChange = { birthdate = it }, 
                        modifier = Modifier.weight(1f), 
                        label = { Text("出生日期") }
                    )
                    WtsOutlinedTextField(
                        value = birthtime, 
                        onValueChange = { birthtime = it }, 
                        modifier = Modifier.weight(1f), 
                        label = { Text("出生時間") },
                        onFocusChanged = { isFocused ->
                            isTimeFieldFocused = isFocused
                            if (!isFocused) birthPeriod = null
                        }
                    )
                }
                
                // 時段選擇按鈕（統一間距）
                if (isTimeFieldFocused) {
                    Spacer(Modifier.height(12.dp))
                    val periods = listOf("凌晨","上午","中午","下午","晚上")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        periods.forEach { p ->
                            val selected = birthPeriod == p
                            WtsFrostedChoiceButton(
                                text = p, 
                                selected = selected, 
                                onClick = { birthPeriod = if (selected) null else p }, 
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                // 顯示最近一次輸入的問題（統一間距）
                if (question.value.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "目前問題：${question.value}", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
            WtsOutlinedTextField(
                value = question.value,
                onValueChange = { question.value = it },
                modifier = Modifier.weight(1f),
                label = { Text("輸入問題（200字內）") },
                singleLine = true,
                supportingText = { Text("${question.value.length}/200") }
            )
            WtsWhiteButton(
                onClick = {
                    if (question.value.isBlank()) return@WtsWhiteButton
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
                    fun normalizeTime(raw: String, period: String?): String? {
                        if (raw.isBlank() && period == null) return null
                        val base = raw.trim()
                        // direct HH:mm
                        if (base.matches(Regex("^\\d{1,2}:\\d{2}$"))) {
                            val parts = base.split(":")
                            val h = parts[0].toInt()
                            val m = parts[1].toInt()
                            if (h in 0..23 && m in 0..59) return String.format("%02d:%02d", h, m)
                            return null
                        }
                        // patterns: 上午10點, 下午3點半, 晚上8點, 中午12點
                        val re = Regex("(凌晨|上午|中午|下午|晚上)?\\s*(\\d{1,2})(點|点)?(?:(?:(:|：)(\\d{1,2}))|(半))?", RegexOption.IGNORE_CASE)
                        val m = re.find(base)
                        if (m != null) {
                            val seg = m.groupValues[1].ifBlank { period ?: "" }
                            var hour = m.groupValues[2].toIntOrNull() ?: return null
                            var minute = when {
                                m.groupValues[6].isNotBlank() -> 30
                                m.groupValues[5].isNotBlank() -> m.groupValues[5].toIntOrNull() ?: 0
                                else -> 0
                            }
                            // map segment to 24h
                            val segNorm = when (seg) {
                                "凌晨" -> 0
                                "上午" -> 0
                                "中午" -> 12
                                "下午" -> 12
                                "晚上" -> 18 // bias to evening; will adjust below
                                else -> null
                            }
                            var h24 = hour
                            if (segNorm != null) {
                                h24 = when (seg) {
                                    "凌晨" -> if (hour == 12) 0 else hour
                                    "上午" -> if (hour == 12) 0 else hour
                                    "中午" -> if (hour < 12) 12 else hour
                                    "下午" -> if (hour < 12) hour + 12 else hour
                                    "晚上" -> if (hour < 12) hour + 12 else hour
                                    else -> hour
                                }
                            }
                            if (h24 in 0..23 && minute in 0..59) return String.format("%02d:%02d", h24, minute)
                        }
                        // only period selected, no specific time
                        if (base.isBlank() && period != null) {
                            return when (period) {
                                "凌晨" -> "01:00"
                                "上午" -> "09:00"
                                "中午" -> "12:00"
                                "下午" -> "15:00"
                                "晚上" -> "20:00"
                                else -> null
                            }
                        }
                        return null
                    }
                    val errors = mutableListOf<String>()
                    // 只有在不跳過個人資料時才驗證個人資料
                    if (!skipPersonalInfo) {
                        if (!validDate(birthdate)) errors.add("出生日期請用 yyyy-MM-dd（例如 1983-01-19）")
                        val normalizedTime = normalizeTime(birthtime, birthPeriod)
                        if (birthtime.isNotBlank() || birthPeriod != null) {
                            if (normalizedTime == null) errors.add("出生時間格式不嚴格限制，可輸入：\n1) HH:mm（例如 08:30）\n2) 上午10點/下午3點半/晚上8點/中午12點\n3) 只選擇時段（凌晨/上午/中午/下午/晚上）")
                        }
                    }
                    if (errors.isNotEmpty()) { dialogMsg = errors.joinToString("\n"); return@WtsWhiteButton }

                    // 先把使用者訊息加入畫面，提升即時回饋感
                    val userQuestion = question.value
                    messages.add(ChatMessage(role = "user", content = userQuestion))

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
                            // 只有在不跳過個人資料時才包含個人資料
                            if (!skipPersonalInfo) {
                                append("【基本資料】")
                                append("\n姓名：").append(name)
                                append(" 年齡：").append(age)
                                append(" 出生地：").append(birthplace)
                                append(" 出生日期：").append(birthdate)
                                val normalizedTime = normalizeTime(birthtime, birthPeriod)
                                if (normalizedTime != null) {
                                    append(" 出生時間：").append(normalizedTime)
                                } else if (!birthtime.isNullOrBlank()) {
                                    append(" 出生時間（原始）：").append(birthtime)
                                } else if (birthPeriod != null) {
                                    append(" 出生時段：").append(birthPeriod)
                                }
                                append("\n【分析規則】請以『該支籤文』為核心，結合紫微斗數的大數據經驗法則（僅根據出生日期、時間與地點的近似經度），給出個人化且審慎的解讀。避免絕對斷語，以『傾向／可能／建議』表述。輸出格式：\n1) 核心解讀：3 點。\n2) 紫微斗數關聯：2~3 點（可提及命宮／事業／財帛／感情等關鍵詞，僅作參考）。\n3) 行動建議：條列 3~5 條。\n4) 避險提醒：2 點。\n字數 200~400。")
                            } else {
                                append("【分析規則】請直接回答用戶問題，給出實用且審慎的建議。避免絕對斷語，以『傾向／可能／建議』表述。輸出格式：\n1) 核心回答：3 點。\n2) 實用建議：條列 3~5 條。\n3) 注意事項：2 點。\n字數 200~400。")
                            }
                            append("\n【問題】").append(userQuestion)
                        }
                        try {
                            val res = withContext(Dispatchers.IO) { ServiceLocator.repository.chat(id, enriched) }
                            res.onSuccess { resp ->
                                // 只追加 AI 回覆，保留完整歷史
                                val assistants = resp.messages.filter { it.role.lowercase().contains("assistant") }
                                if (assistants.isNotEmpty()) {
                                    messages.addAll(assistants)
                                    WtsLogger.i("Chat response received: ${assistants.size} messages")
                                } else {
                                    error.value = "未收到有效回應"
                                }
                            }.onFailure { e ->
                                WtsLogger.e("chat() failed: ${e.message}", e)
                                error.value = "聊天服務錯誤：${e.message}"
                            }
                        } catch (e: Exception) {
                            WtsLogger.e("Unexpected chat error: ${e.message}", e)
                            error.value = "未預期的錯誤：${e.message}"
                        }
                        loading.value = false
                        question.value = ""
                    }
                },
                enabled = !loading.value,
                text = if (loading.value) "傳送中..." else "發送"
            )
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
            WtsWhiteButton(text = "返回", onClick = { nav.navigateUp() })
            WtsWhiteButton(text = "回籤文", onClick = { nav.navigate("content/${id}") })
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


