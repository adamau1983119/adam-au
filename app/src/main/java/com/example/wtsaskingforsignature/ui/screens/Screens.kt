package com.example.wtsaskingforsignature.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.wtsaskingforsignature.Routes
import com.example.wtsaskingforsignature.data.ServiceLocator
import com.example.wtsaskingforsignature.data.api.ChatMessage
import com.example.wtsaskingforsignature.data.api.DrawResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import com.example.wtsaskingforsignature.util.WtsLogger
// import com.example.wtsaskingforsignature.util.ApiTester
import com.example.wtsaskingforsignature.util.LanguageManager
import com.example.wtsaskingforsignature.util.LanguageManager.Language
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.wtsaskingforsignature.R
import com.example.wtsaskingforsignature.ui.theme.MdGradientBottom
import com.example.wtsaskingforsignature.ui.theme.MdGradientTop
import com.example.wtsaskingforsignature.ui.components.WtsPrimaryButton
import com.example.wtsaskingforsignature.ui.components.WtsOutlinedButton
import com.example.wtsaskingforsignature.ui.components.WtsWhiteButton
import com.example.wtsaskingforsignature.ui.components.WtsFrostedChoiceButton
import android.os.Build
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
 

@Composable
private fun CategorySelector(selected: String?, onSelect: (String) -> Unit) {
	val categories = listOf("事業","財運","自身","家庭","婚姻","遷徙","名譽","健康","友誼","其他")
	categories.chunked(3).forEach { row ->
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			row.forEach { c ->
				val isSelected = selected == c
				com.example.wtsaskingforsignature.ui.components.WtsFrostedChoiceButton(
					text = c,
					selected = isSelected,
					onClick = { onSelect(c) },
					modifier = Modifier.weight(1f)
				)
			}
		}
		Spacer(Modifier.height(8.dp))
	}
}

@Composable
private fun StepItem(index: Int, text: androidx.compose.ui.text.AnnotatedString) {
	Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
		Box(
			modifier = Modifier.size(22.dp).background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape),
			contentAlignment = Alignment.Center
		) {
			Text("${index}", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
		}
		Text(text, style = MaterialTheme.typography.bodyMedium)
	}
}

@Composable
private fun FlowSteps(currentStepIndex: Int) {
	val steps = listOf("選擇占卜類別", "求籤步驟", "抽中簽預覽", "擲杯")
	Column(Modifier.fillMaxWidth()) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			steps.forEachIndexed { index, _ ->
				val isDone = index < currentStepIndex
				val isCurrent = index == currentStepIndex
				val circleBg = when {
					isCurrent -> MaterialTheme.colorScheme.primary
					isDone -> MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
					else -> MaterialTheme.colorScheme.surfaceVariant
				}
				val circleFg = if (isCurrent || isDone) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

				Box(contentAlignment = Alignment.Center) {
					Box(
						modifier = Modifier.size(28.dp)
							.background(circleBg, shape = androidx.compose.foundation.shape.CircleShape),
						contentAlignment = Alignment.Center
					) {
						Text(if (isDone) "✓" else "${index + 1}", color = circleFg, style = MaterialTheme.typography.labelMedium)
					}
				}
				if (index != steps.lastIndex) {
					Box(
						modifier = Modifier
							.width(0.dp)
					)
				}
			}
		}
		Spacer(Modifier.height(6.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			steps.forEachIndexed { index, label ->
				Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
					Text(label, style = MaterialTheme.typography.labelSmall, color = if (index <= currentStepIndex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
				}
			}
		}
		Spacer(Modifier.height(8.dp))
		Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
			steps.forEachIndexed { index, _ ->
				if (index != steps.lastIndex) {
					val lineColor = if (index < currentStepIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
					Box(modifier = Modifier.weight(1f).height(2.dp).background(lineColor))
				} else {
					Spacer(modifier = Modifier.width(0.dp))
				}
			}
		}
	}
}

private fun removeFortuneOmenLine(raw: String?): String? {
	if (raw.isNullOrBlank()) return raw
	val filtered = raw
		.split('\n')
		.filterNot { it.contains("求籤吉凶") || it.contains("求签吉凶") }
		.joinToString("\n")
		.trim()
	return if (filtered.isBlank()) null else filtered
}

@Composable
fun HomeScreen(nav: NavHostController) {
	val context = LocalContext.current
	val imageResId = remember {
		context.resources.getIdentifier("wong_tai_sin", "drawable", context.packageName)
	}
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text("黃大仙靈簽", style = MaterialTheme.typography.headlineMedium)
		Spacer(Modifier.height(16.dp))
		if (imageResId != 0) {
			Image(
				painter = painterResource(id = imageResId),
				contentDescription = "黃大仙插圖",
				modifier = Modifier.fillMaxWidth().height(220.dp)
			)
		} else {
			Text("請將圖片檔放入 res/drawable/ 並命名為 wong_tai_sin.png")
		}
		Spacer(Modifier.height(24.dp))
		DirectDrawGifButton(onClick = { nav.navigate(Routes.DIRECT_DRAW) })
		Spacer(Modifier.height(12.dp))
		WtsWhiteButton(text = "摘杯抽籤", onClick = { nav.navigate(Routes.CUP_DRAW) }, modifier = Modifier.fillMaxWidth())
		Spacer(Modifier.height(12.dp))
		WtsWhiteButton(text = "每日一籤", onClick = { nav.navigate(Routes.DAILY) }, modifier = Modifier.fillMaxWidth())
		Spacer(Modifier.height(12.dp))
		WtsWhiteButton(text = "Deep Seek解籤", onClick = { nav.navigate(Routes.BROWSE) }, modifier = Modifier.fillMaxWidth())
		Spacer(Modifier.height(12.dp))
		WtsWhiteButton(text = "設置", onClick = { nav.navigate(Routes.SETTINGS) }, modifier = Modifier.fillMaxWidth())
	}
}

@Composable
private fun DirectDrawGifButton(onClick: () -> Unit) {
    com.example.wtsaskingforsignature.ui.components.WtsWhiteButton(text = "直接求籤", onClick = onClick, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun LanguageSelectorButton() {
    val context = LocalContext.current
    val currentLanguage = LanguageManager.getCurrentLanguage(context)
    val showLanguageDialog = remember { mutableStateOf(false) }
    
    WtsWhiteButton(
        text = "語言: ${currentLanguage.displayName}",
        onClick = { showLanguageDialog.value = true },
        modifier = Modifier.fillMaxWidth()
    )
    
    if (showLanguageDialog.value) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { language ->
                LanguageManager.setLanguage(context, language)
                showLanguageDialog.value = false
            },
            onDismiss = { showLanguageDialog.value = false }
        )
    }
}

@Composable
fun DirectDrawScreen(nav: NavHostController) {
	val selected = remember { mutableStateOf<String?>(null) }
	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("直接求籤", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(8.dp))
		FlowSteps(currentStepIndex = 0)
		Spacer(Modifier.height(12.dp))
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current)
				.data(R.raw.wts02)
				.decoderFactory(if (Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory() else GifDecoder.Factory())
				.build(),
			contentDescription = "示例動畫",
			modifier = Modifier.size(360.dp).align(Alignment.CenterHorizontally)
		)
		Spacer(Modifier.height(12.dp))
		Text("請選擇所求之事：")
		Spacer(Modifier.height(8.dp))
		CategorySelector(selected.value) { selected.value = it }
		Spacer(Modifier.height(12.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			WtsWhiteButton(text = "返回首頁", onClick = { nav.popBackStack() })
			WtsWhiteButton(text = "下一步", onClick = { nav.navigate(Routes.STEPS) }, enabled = selected.value != null)
		}
	}
}

@Composable
fun CupDrawScreen(nav: NavHostController) {
	val selected = remember { mutableStateOf<String?>(null) }
	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("摘杯抽籤", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(8.dp))
		FlowSteps(currentStepIndex = 0)
		Spacer(Modifier.height(8.dp))
		Text("提示：需完成三次擲杯以確認籤意", style = MaterialTheme.typography.bodySmall)
		Spacer(Modifier.height(8.dp))
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current)
				.data(R.raw.wts02)
				.decoderFactory(if (Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory() else GifDecoder.Factory())
				.build(),
			contentDescription = "示例動畫",
			modifier = Modifier.size(360.dp).align(Alignment.CenterHorizontally)
		)
		Spacer(Modifier.weight(1f))
		Text("請選擇所求之事：")
		Spacer(Modifier.height(8.dp))
		CategorySelector(selected.value) { selected.value = it }
		Spacer(Modifier.height(12.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			WtsWhiteButton(text = "返回首頁", onClick = { nav.popBackStack() })
			WtsWhiteButton(text = "下一步", onClick = { nav.navigate(Routes.STEPS) }, enabled = selected.value != null)
		}
	}
}

@Composable
fun DailyScreen(nav: NavHostController) {
	val alreadyDrawn = remember { mutableStateOf(false) }
	val selected = remember { mutableStateOf<String?>(null) }
	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("每日一籤", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(8.dp))
		FlowSteps(currentStepIndex = 0)
		Spacer(Modifier.height(12.dp))
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current)
				.data(R.raw.wts02)
				.decoderFactory(if (Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory() else GifDecoder.Factory())
				.build(),
			contentDescription = "示例動畫",
			modifier = Modifier.size(360.dp).align(Alignment.CenterHorizontally)
		)
		Spacer(Modifier.height(12.dp))
		Spacer(Modifier.weight(1f))
		Text("請選擇所求之事：")
		Spacer(Modifier.height(8.dp))
		CategorySelector(selected.value) { selected.value = it }
		Spacer(Modifier.height(12.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			WtsWhiteButton(text = "返回首頁", onClick = { nav.popBackStack() })
			WtsWhiteButton(text = if (alreadyDrawn.value) "今日已抽" else "抽今日之籤", onClick = {
				if (!alreadyDrawn.value && selected.value != null) {
					alreadyDrawn.value = true
					nav.navigate(Routes.STEPS)
				}
			}, enabled = !alreadyDrawn.value && selected.value != null)
		}
	}
}

@Composable
fun BrowseScreen(nav: NavHostController) {
	val items = (1..100).map { it }
	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("Deep Seek解籤", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(12.dp))
		Text("1.請選擇靈籤編號")
		Spacer(Modifier.height(8.dp))
		Text("清單（1~100）：點擊編號查看全文")
		Spacer(Modifier.height(12.dp))
		val columns = 10
		val rows = (items.size + columns - 1) / columns
		for (r in 0 until rows) {
			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
				for (c in 0 until columns) {
					val index = r * columns + c
					if (index < items.size) {
						val id = items[index]
						OutlinedButton(
							onClick = {
								WtsLogger.i("Browse click id=$id")
								nav.navigate(Routes.content(id))
							},
							modifier = Modifier
								.weight(1f)
								.aspectRatio(1f),
							shape = androidx.compose.foundation.shape.CircleShape,
							contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp),
							colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
								containerColor = Color.White.copy(alpha = 0.5f)
							)
						) {
							Text(text = "#${id}", maxLines = 1, style = MaterialTheme.typography.labelSmall)
						}
					} else {
						Spacer(modifier = Modifier.weight(1f))
					}
				}
			}
			Spacer(Modifier.height(6.dp))
		}
		Spacer(Modifier.height(16.dp))
		OutlinedButton(onClick = { nav.popBackStack() }) { Text("返回首頁") }
	}
}

@Composable
fun CategoryScreen(nav: NavHostController) {
	val categories = listOf("事業","財運","自身","家庭","婚姻","遷徙","名譽","健康","友誼","其他")
	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("選擇占卜類別", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(12.dp))
		Text("請選擇所求之事：")
		Spacer(Modifier.height(12.dp))
		val selected = remember { mutableStateOf<String?>(null) }
		categories.chunked(3).forEach { row ->
			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				row.forEach { c ->
					OutlinedButton(onClick = { selected.value = c }, modifier = Modifier.weight(1f)) { Text(c) }
				}
			}
			Spacer(Modifier.height(8.dp))
		}
		Spacer(Modifier.height(12.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			OutlinedButton(onClick = { nav.popBackStack() }) { Text("返回") }
			Button(onClick = { nav.navigate(Routes.STEPS) }, enabled = selected.value != null) { Text("下一步") }
		}
	}
}

@Composable
fun StepsScreen(nav: NavHostController) {
	val context = LocalContext.current
	val imageResId = remember { context.resources.getIdentifier("wong_tai_sin", "drawable", context.packageName) }
	Column(
		Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		Text("求籤步驟", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
		Spacer(Modifier.height(12.dp))
		if (imageResId != 0) {
			Image(
				painter = painterResource(id = imageResId),
				contentDescription = "黃大仙插圖",
				modifier = Modifier.fillMaxWidth().height(180.dp)
			)
			Spacer(Modifier.height(12.dp))
		}
		// 美化的三步驟
		StepItem(1, buildAnnotatedString {
			withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("雙手合十") }
			append("，默念「")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("黃大仙有求必應") }
			append("」3次")
		})
		Spacer(Modifier.height(6.dp))
		StepItem(2, buildAnnotatedString {
			append("默念")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("姓名") }
			append("、")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("出生地與時間") }
			append("、")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("年齡") }
			append("與")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("所求之事") }
		})
		Spacer(Modifier.height(6.dp))
		StepItem(3, buildAnnotatedString {
			append("點擊下方按鈕")
			withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("開始求籤") }
		})
		Spacer(Modifier.height(12.dp))
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current)
				.data(R.raw.wts02)
				.apply {
					if (Build.VERSION.SDK_INT >= 28) {
						decoderFactory(ImageDecoderDecoder.Factory())
					} else {
						decoderFactory(GifDecoder.Factory())
					}
				}
				.crossfade(true)
				.allowHardware(false)
				.build(),
			contentDescription = "求籤動畫",
			modifier = Modifier
				.size(360.dp)
				.align(Alignment.CenterHorizontally)
		)
		Spacer(Modifier.height(12.dp))
		com.example.wtsaskingforsignature.ui.components.WtsWhiteButton(text = "開始求籤", onClick = {
			WtsLogger.i("StepsScreen click: navigate to PREVIEW")
			try {
				nav.navigate(Routes.PREVIEW)
			} catch (e: Exception) {
				WtsLogger.e("StepsScreen navigate error: ${'$'}{e.message}", e)
			}
		}, modifier = Modifier.fillMaxWidth())
		Spacer(Modifier.height(12.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			com.example.wtsaskingforsignature.ui.components.WtsWhiteButton(text = "上一步", onClick = { nav.popBackStack() })
			com.example.wtsaskingforsignature.ui.components.WtsWhiteButton(text = "返回", onClick = { nav.navigateUp() })
		}
	}
}

@Composable
fun PreviewScreen(nav: NavHostController) {
	val loading = remember { mutableStateOf(true) }
	val error = remember { mutableStateOf<String?>(null) }
	val data = remember { mutableStateOf<DrawResponse?>(null) }

	LaunchedEffect(Unit) {
		WtsLogger.i("PreviewScreen start draw()")
		val result = withContext(Dispatchers.IO) { ServiceLocator.repository.draw() }
		result.onSuccess { d ->
			WtsLogger.d("draw() success id=${'$'}{d.id}")
			data.value = d
			error.value = null
		}.onFailure { e ->
			WtsLogger.e("draw() failed: ${'$'}{e.message}")
			error.value = e.message
		}
		loading.value = false
	}

	Column(Modifier.fillMaxSize().padding(16.dp)) {
		val displayId = data.value?.id?.toString() ?: "--"
		val displayName = data.value?.title?.takeIf { !it.isNullOrBlank() } ?: ""
		val header = if (displayName.isNotEmpty()) "第 ${displayId} 靈簽：${displayName}" else "第 ${displayId} 靈簽"
		Text(header, style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.CenterHorizontally))
		Spacer(Modifier.height(12.dp))
		val cleanedSummary = removeFortuneOmenLine(data.value?.summary)
		if (!cleanedSummary.isNullOrBlank()) {
			Text(cleanedSummary)
		} else if (loading.value) {
			Text("載入中...")
		} else if (!error.value.isNullOrBlank()) {
			Text(error.value!!)
		}
		Spacer(Modifier.height(24.dp))
		Text("擲杯驗證（需完成三次）：")
		Spacer(Modifier.height(12.dp))
		// 顯示 240dp GIF（優先 wts06，找不到則退回 wts02）
		val ctx = LocalContext.current
		val gifResId = remember {
			val idRaw = ctx.resources.getIdentifier("wts06", "raw", ctx.packageName)
			val idDrawable = if (idRaw == 0) ctx.resources.getIdentifier("wts06", "drawable", ctx.packageName) else 0
			when {
				idRaw != 0 -> idRaw
				idDrawable != 0 -> idDrawable
				else -> R.raw.wts02
			}
		}
		AsyncImage(
			model = ImageRequest.Builder(ctx)
				.data(gifResId)
				.decoderFactory(if (Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory() else GifDecoder.Factory())
				.build(),
			contentDescription = "擲筊動畫",
			modifier = Modifier.size(240.dp).align(Alignment.CenterHorizontally)
		)
		Spacer(Modifier.height(12.dp))
		WtsWhiteButton(text = "擲筊", onClick = { nav.navigate(Routes.cup(data.value?.id ?: 1)) }, modifier = Modifier.fillMaxWidth())
		Spacer(Modifier.height(12.dp))
		WtsWhiteButton(text = "直接查看籤文", onClick = { nav.navigate(Routes.content(data.value?.id ?: 1)) })
		Spacer(Modifier.weight(1f))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			WtsWhiteButton(text = "返回", onClick = { nav.popBackStack() })
			WtsWhiteButton(text = "下一步", onClick = { nav.navigate(Routes.CUP) })
		}
	}
}

@Composable
fun CupScreen(nav: NavHostController, id: Int) {
	val choices = listOf(R.drawable.wts03, R.drawable.wts04, R.drawable.wts05)
	val rolls = remember { mutableStateOf(listOf<Int>()) }

	fun regenerate() {
		val newRolls = List(3) { choices.random() }
		rolls.value = newRolls
	}

	LaunchedEffect(Unit) { regenerate() }

	val countWts03 = rolls.value.count { it == R.drawable.wts03 }
	LaunchedEffect(rolls.value) {
		if (countWts03 >= 2) {
			WtsLogger.i("CupScreen auto navigate to Content due to >=2 wts03")
			nav.navigate(Routes.content(id))
		}
	}

	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("擲筊", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
		Spacer(Modifier.height(16.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
			rolls.value.forEach { resId ->
				Image(
					painter = painterResource(id = resId),
					contentDescription = null,
					modifier = Modifier.weight(1f).height(120.dp)
				)
			}
		}
		Spacer(Modifier.height(16.dp))
		if (countWts03 < 2) {
			Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				WtsWhiteButton(text = "直接查看簽文", onClick = { nav.navigate(Routes.content(id)) }, modifier = Modifier.weight(1f))
				WtsPrimaryButton(text = "重新擲筊", onClick = { regenerate() }, modifier = Modifier.weight(1f))
			}
		}
		Spacer(Modifier.weight(1f))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			WtsWhiteButton(text = "返回抽籤", onClick = { nav.navigate(Routes.DIRECT_DRAW) })
			WtsWhiteButton(text = "回首頁", onClick = { nav.navigate(Routes.HOME) })
		}
	}
}

@Composable
fun CupResultScreen(nav: NavHostController, valid: Boolean) {
	val info = if (valid) "系統判定：生效" else "系統判定：未生效，請重新抽籤"
	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("擲杯結果", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(12.dp))
		Text(info)
		Spacer(Modifier.height(24.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			WtsWhiteButton(text = "重新抽籤", onClick = { nav.navigate(Routes.DIRECT_DRAW) })
			WtsWhiteButton(text = "查看籤文內容", onClick = { nav.navigate(Routes.content(1)) })
		}
	}
}

@Composable
fun ContentScreen(nav: NavHostController, id: Int) {
	val loading = remember { mutableStateOf(true) }
	val error = remember { mutableStateOf<String?>(null) }
	val content = remember { mutableStateOf<DrawResponse?>(null) }

	LaunchedEffect(id) {
		WtsLogger.i("ContentScreen loading id=${id}")
		val res = withContext(Dispatchers.IO) { ServiceLocator.repository.fortune(id) }
		res.onSuccess { d ->
			WtsLogger.d("Content loaded: id=${d.id} title=${d.title}")
			content.value = d
			error.value = null
		}.onFailure { e ->
			WtsLogger.e("Content load failed: ${e.message}")
			error.value = e.message
		}
		loading.value = false
	}

	Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
		val titleText = content.value?.title?.takeIf { !it.isNullOrBlank() } ?: "第 ${content.value?.id ?: id} 籤"
		Text(titleText, style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(12.dp))
		val summary = content.value?.summary
		if (!summary.isNullOrBlank()) {
			Text(summary, style = MaterialTheme.typography.bodyMedium)
			Spacer(Modifier.height(16.dp))
		}
		val raw = content.value?.content
		if (!raw.isNullOrBlank()) {
			val lines = raw.split('\n')
			val verse = mutableListOf<String>()
			val omen = mutableListOf<Pair<String,String>>()
			val explain = mutableListOf<Pair<String,String>>()
			var current: String? = null
			lines.forEach { s ->
				val line = s.trim()
				when {
					line.startsWith("籤詩") -> current = "verse"
					line.startsWith("仙機") -> current = "omen"
					line.startsWith("釋義") -> current = "explain"
					line.isBlank() -> {}
					else -> {
						when (current) {
							"verse" -> {
								// 保留「算命籤詩」行，供後續渲染時做特別樣式
								verse.add(line)
							}
							"omen" -> {
								val p = line.split('：', '︰', ':', limit = 2)
								if (p.size == 2) omen.add(p[0] to p[1]) else omen.add("" to line)
							}
							"explain" -> {
								val p = line.split('：', '︰', ':', limit = 2)
								if (p.size == 2) explain.add(p[0] to p[1]) else explain.add("" to line)
							}
							else -> verse.add(line)
						}
					}
				}
			}
			if (verse.isNotEmpty()) {
				Text("籤詩：", style = MaterialTheme.typography.titleMedium)
				Spacer(Modifier.height(6.dp))
				var special = false
				var specialCount = 0
				val normalStyle = MaterialTheme.typography.titleMedium
				val bigStyle = normalStyle.copy(fontSize = normalStyle.fontSize * 1.5f)
				verse.forEach { v ->
					val isMarker = v.contains("算命籤詩") || v.contains("算命签诗")
					if (isMarker) {
						// 標題本身按一般樣式顯示，並開啟後續兩行加大、第三行空白
						Text(v, style = normalStyle)
						Spacer(Modifier.height(4.dp))
						special = true
						specialCount = 0
					} else if (special) {
						when (specialCount) {
							0, 1 -> { Text(v, style = bigStyle); Spacer(Modifier.height(4.dp)) }
							2 -> { Spacer(Modifier.height(8.dp)) }
							else -> { Text(v, style = normalStyle); Spacer(Modifier.height(4.dp)) }
						}
						specialCount += 1
						if (specialCount > 2) {
							special = false
						}
					} else {
						Text(v, style = normalStyle)
						Spacer(Modifier.height(4.dp))
					}
				}
				Spacer(Modifier.height(12.dp))
			}
			if (omen.isNotEmpty()) {
				Text("仙機：", style = MaterialTheme.typography.titleMedium)
				Spacer(Modifier.height(6.dp))
				omen.forEach { (k, v) ->
					val needsTopSpace = k.contains("黃大位算命解籤詩") || k.contains("黃大仙算命解籤詩") || k.contains("黃大仙算命解運勢") || k.contains("黄大仙算命解运势")
					if (needsTopSpace) Spacer(Modifier.height(8.dp))
					if (k.isBlank()) Text(v) else Text("${k}：$v")
					Spacer(Modifier.height(4.dp))
				}
				Spacer(Modifier.height(12.dp))
			}
			if (explain.isNotEmpty()) {
				Text("釋義：", style = MaterialTheme.typography.titleMedium)
				Spacer(Modifier.height(6.dp))
				explain.forEach { (k, v) ->
					val needsTopSpace = k.contains("黃大位算命解籤詩") || k.contains("黃大仙算命解籤詩") || k.contains("黃大仙算命解運勢") || k.contains("黄大仙算命解运势")
					if (needsTopSpace) Spacer(Modifier.height(8.dp))
					if (k.isBlank()) Text(v) else Text("${k}：$v")
					Spacer(Modifier.height(4.dp))
				}
			}
		} else {
			Text(if (loading.value) "載入中..." else error.value ?: "無內容（請確認 assets/fortunes.json 是否有 id=${id} 的條目）")
		}
		Spacer(Modifier.height(24.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
			WtsWhiteButton(
				text = "Deep Seek解籤",
				onClick = {
					// 傳遞籤文內容與標題給新版對話頁，作為 DeepSeek 解籤依據
					val rawForChat = content.value?.content ?: ""
					val titleForChat = titleText
					nav.currentBackStackEntry?.savedStateHandle?.set("chat_context", rawForChat)
					nav.currentBackStackEntry?.savedStateHandle?.set("chat_title", titleForChat)
					nav.navigate(com.example.wtsaskingforsignature.Routes.chatNew(id))
				},
				modifier = Modifier.fillMaxWidth()
			)
		}
		Spacer(Modifier.height(24.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			WtsWhiteButton(text = "返回", onClick = { nav.navigateUp() })
			WtsWhiteButton(text = "回首頁", onClick = { nav.navigate(Routes.HOME) })
		}
	}
}

@Composable
fun ChatScreen(nav: NavHostController, id: Int) {
	val scope = rememberCoroutineScope()
	val messages = remember { mutableStateListOf<ChatMessage>() }
	val question = remember { mutableStateOf("") }
	val loading = remember { mutableStateOf(false) }
	val error = remember { mutableStateOf<String?>(null) }

	Column(Modifier.fillMaxSize().padding(16.dp)) {
		Text("對話界面（DeepSeek） — 第 $id 籤", style = MaterialTheme.typography.titleLarge)
		Spacer(Modifier.height(12.dp))
		Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
			messages.forEach { message ->
				Text("[${message.role}] ${message.content}")
				Spacer(Modifier.height(8.dp))
			}
			if (error.value != null) {
				Text("錯誤：${'$'}{error.value}")
			}
		}
		Spacer(Modifier.height(12.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
			OutlinedTextField(value = question.value, onValueChange = { question.value = it }, modifier = Modifier.weight(1f), label = { Text("輸入問題（200字內）") })
			Button(onClick = {
				if (question.value.isBlank()) return@Button
				loading.value = true
				error.value = null
				scope.launch {
					WtsLogger.i("ChatScreen chat() id=${'$'}id")
					val res = withContext(Dispatchers.IO) { ServiceLocator.repository.chat(id, question.value) }
					res.onSuccess { resp ->
						WtsLogger.d("chat() success messages=${'$'}{resp.messages.size}")
						messages.clear()
						messages.addAll(resp.messages)
						question.value = ""
					}.onFailure { e ->
						WtsLogger.e("chat() failed: ${'$'}{e.message}")
						error.value = e.message
					}
					loading.value = false
				}
			}, enabled = !loading.value) { Text(if (loading.value) "傳送中..." else "發送") }
		}
		Spacer(Modifier.height(12.dp))
		Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
			OutlinedButton(onClick = { nav.navigate(Routes.content(id)) }) { Text("返回籤文") }
			OutlinedButton(onClick = { nav.navigate(Routes.HOME) }) { Text("回首頁") }
		}
	}
}

/**
 * 語言選擇對話框
 */
@Composable
private fun LanguageSelectionDialog(
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("選擇語言") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageManager.getAllSupportedLanguages().forEach { language ->
                    LanguageOptionCard(
                        language = language,
                        isSelected = language == currentLanguage,
                        onClick = { onLanguageSelected(language) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

/**
 * 語言選項卡片
 */
@Composable
private fun LanguageOptionCard(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
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
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
