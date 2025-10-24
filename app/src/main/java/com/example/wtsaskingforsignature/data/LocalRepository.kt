package com.example.wtsaskingforsignature.data

import com.example.wtsaskingforsignature.WtsApp
import com.example.wtsaskingforsignature.data.api.ChatMessage
import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.data.api.CupAttempt
import com.example.wtsaskingforsignature.data.api.CupResultResponse
import com.example.wtsaskingforsignature.data.api.DrawResponse
import com.example.wtsaskingforsignature.util.WtsLogger
// import com.squareup.moshi.Moshi
// import com.squareup.moshi.Types
// import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.BufferedReader
import java.io.StringReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.charset.Charset
import com.example.wtsaskingforsignature.core.DrawEngine
import kotlin.random.Random

class LocalRepository : Repository {
	@Volatile
	private var loaded: Boolean = false

	private var fortunes: List<DrawResponse> = (1..100).map { id ->
		DrawResponse(
			id = id,
			title = "第${'$'}id籤",
			summary = "（待補）",
			content = "（待補全文）"
		)
	}

	/**
	 * 移除內容中的標籤，保持原始顯示格式
	 * Version 21 專用：過濾 [A1], [A2-1], [A2-2], [A3], [B], [C] 等標籤
	 */
	private fun removeTagsFromContent(content: String): String {
		if (content.isBlank()) return content
		
		return content.lines().joinToString("\n") { line ->
			// 移除行首的標籤，如 [A1], [A2-1], [A2-2] 等
			line.replace(Regex("^\\[A\\d+(-\\d+)?\\]"), "")
				.replace(Regex("^\\[[A-P]\\]"), "")
				.trim()
		}.trim()
	}

	private fun decodeWithBestCharset(bytes: ByteArray): String? {
		val candidates = mutableListOf<Charset>()
		candidates += StandardCharsets.UTF_8
		try { candidates += Charset.forName("UTF-16LE") } catch (_: Throwable) {}
		try { candidates += Charset.forName("UTF-16BE") } catch (_: Throwable) {}
		listOf("Big5", "MS950", "GB18030").forEach { name ->
			try { candidates += Charset.forName(name) } catch (_: Throwable) {}
		}
		var bestText: String? = null
		var bestScore = Int.MAX_VALUE
		for (cs in candidates) {
			try {
				val text = String(bytes, cs)
				val bad = text.count { it == '\uFFFD' }
				if (bad < bestScore) {
					bestScore = bad
					bestText = text
					if (bad == 0) break
				}
			} catch (_: Throwable) {}
		}
		if (bestText == null) return null
		if (bestScore > 0) {
			WtsLogger.w("CSV decoding had $bestScore replacement chars; charset fallback used")
		}
		return bestText
	}

	private fun tryLoadFromAssetsOnce() {
		if (loaded) return
		synchronized(this) {
			if (loaded) return
			// 1) 優先讀取 CSV（fortunes_source.csv）
			val csvLoaded = tryLoadFromCsv()
			if (!csvLoaded) {
				// 2) 再讀取 JSON（fortunes.json）
				tryLoadFromJson()
			}
			loaded = true
		}
	}

	private fun tryLoadFromCsv(): Boolean {
		return try {
			val context = WtsApp.instance
			// Version 21 使用標籤化的籤文數據文件
			val assetName = if (com.example.wtsaskingforsignature.BuildConfig.VERSION_CODE >= 21) {
				"fortunes_source_v21.csv"
			} else {
				"fortunes_source.csv"
			}
			val assetList = context.assets.list("")?.toList() ?: emptyList()
			if (!assetList.contains(assetName)) {
				WtsLogger.i("CSV not found: $assetName, skip")
				return false
			}
			
			// 使用更简单的CSV解析方法
			context.assets.open(assetName).use { input ->
				val bytes = input.readBytes()
				val text = decodeWithBestCharset(bytes) ?: return false
				WtsLogger.i("CSV text length: ${text.length}")
				
				// 简单的行分割，处理引号内的换行
				val lines = parseCsvWithQuotes(text)
				WtsLogger.i("Parsed CSV lines: ${lines.size}")
				
				if (lines.size < 2) {
					WtsLogger.w("CSV too few lines: ${lines.size}")
					return false
				}
				
				// 检查表头
				val header = lines[0]
				val expected = listOf("id","title","summary","content")
				val headerCells = header.map { it.trim() }
				WtsLogger.i("Header: $headerCells")
				
				if (headerCells.map { it.lowercase() } != expected) {
					WtsLogger.w("Header mismatch: expected=$expected, got=${headerCells.map { it.lowercase() }}")
					return false
				}
				
				val result = mutableListOf<DrawResponse>()
				val errors = mutableListOf<String>()
				
				lines.drop(1).forEachIndexed { idx, row ->
					if (row.size < 4) {
						errors += "line ${idx+2}: columns < 4 (got ${row.size})"
						return@forEachIndexed
					}
					
					val id = row[0].toIntOrNull()
					val title = row[1].trim()
					val summary = row[2].trim()
					val content = row[3].trim()
					
					if (id == null || id !in 1..100) {
						errors += "line ${idx+2}: invalid id=${row[0]}"
						return@forEachIndexed
					}
					
					if (title.isBlank() || summary.isBlank() || content.isBlank()) {
						errors += "line ${idx+2}: empty field"
						return@forEachIndexed
					}
					
					result += DrawResponse(id = id, title = title, summary = summary, content = content)
					
					if (idx < 3) {
						WtsLogger.i("Line ${idx+2}: id=$id, title='${title.take(30)}'")
					}
				}
				
				if (result.isNotEmpty()) {
					fortunes = result.sortedBy { it.id }
					WtsLogger.i("CSV loaded ${result.size} fortunes, errors=${errors.size}")
					if (errors.isNotEmpty()) {
						errors.take(5).forEach { WtsLogger.w(it) }
					}
					return true
				} else {
					WtsLogger.w("No fortunes loaded from CSV")
				}
			}
			false
		} catch (t: Throwable) {
			WtsLogger.e("CSV load exception: ${t.message}")
			t.printStackTrace()
			false
		}
	}
	
	private fun parseCsvWithQuotes(text: String): List<List<String>> {
		val result = mutableListOf<List<String>>()
		val lines = text.lines()
		var currentRow = mutableListOf<String>()
		var currentField = StringBuilder()
		var inQuotes = false
		
		for (line in lines) {
			var i = 0
			while (i < line.length) {
				val ch = line[i]
				when (ch) {
					'"' -> {
						if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
							// 转义双引号
							currentField.append('"')
							i++
						} else {
							inQuotes = !inQuotes
						}
					}
					',' -> {
						if (inQuotes) {
							currentField.append(ch)
						} else {
							currentRow.add(currentField.toString().trim())
							currentField.clear()
						}
					}
					else -> currentField.append(ch)
				}
				i++
			}
			
			// 如果不在引号中，说明这一行结束了
			if (!inQuotes) {
				currentRow.add(currentField.toString().trim())
				result.add(currentRow.toList())
				currentRow.clear()
				currentField.clear()
			} else {
				// 在引号中，添加换行符并继续
				currentField.append('\n')
			}
		}
		
		return result
	}

	private fun readCsvLogicalLines(br: BufferedReader): List<String> {
		val logicalLines = mutableListOf<String>()
		val sb = StringBuilder()
		var inQuotes = false
		while (true) {
			val line = br.readLine() ?: break
			// 先把這一行附加到緩衝
			if (sb.isNotEmpty()) sb.append('\n')
			sb.append(line)
			// 走訪此行，更新引號狀態（處理 "" 轉義）
			var i = 0
			while (i < line.length) {
				val ch = line[i]
				if (ch == '"') {
					if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
						i++ // 跳過轉義的第二個引號
					} else {
						inQuotes = !inQuotes
					}
				}
				i++
			}
			// 若目前不在引號中，代表一筆記錄結束
			if (!inQuotes) {
				logicalLines += sb.toString()
				sb.clear()
			}
		}
		// 若檔尾仍有殘留（例如最後一筆沒有換行），也納入
		if (sb.isNotEmpty()) logicalLines += sb.toString()
		return logicalLines
	}

	private fun tryLoadFromJson() {
		// JSON加载功能已移除，只使用CSV数据源
		WtsLogger.i("JSON loading disabled, using CSV only")
	}

	private fun splitCsvLine(line: String): List<String> {
		val result = mutableListOf<String>()
		val sb = StringBuilder()
		var inQuotes = false
		var i = 0
		while (i < line.length) {
			val ch = line[i]
			when (ch) {
				'"' -> {
					if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
						// 轉義雙引號
						sb.append('"')
						i++
					} else {
						inQuotes = !inQuotes
					}
				}
				',' -> {
					if (inQuotes) {
						sb.append(ch)
					} else {
						result += sb.toString().trim()
						sb.clear()
					}
				}
				else -> sb.append(ch)
			}
			i++
		}
		// 添加最后一个字段
		result += sb.toString().trim()
		return result
	}

	override suspend fun draw(): Result<DrawResponse> = runCatching {
		tryLoadFromAssetsOnce()
		val id = DrawEngine.draw(1, 100)
		fortunes.first { it.id == id }
	}

	override suspend fun fortune(id: Int): Result<DrawResponse> = runCatching {
		tryLoadFromAssetsOnce()
		WtsLogger.i("Looking for fortune id=$id, total fortunes=${fortunes.size}")
		val fortune = fortunes.firstOrNull { it.id == id }
		if (fortune == null) {
			WtsLogger.e("Fortune id=$id not found!")
			WtsLogger.e("Available fortune IDs: ${fortunes.map { it.id }}")
		} else {
			WtsLogger.i("Found fortune: ${fortune.title}")
		}
		val result = fortune ?: fortunes.first { it.id == 1 } // fallback to first fortune
		
		// Version 21: 過濾標籤，保持原始顯示格式
		if (com.example.wtsaskingforsignature.BuildConfig.VERSION_CODE >= 21) {
			result.copy(
				content = removeTagsFromContent(result.content ?: "")
			)
		} else {
			result
		}
	}

	override suspend fun cupResult(): Result<CupResultResponse> = runCatching {
		val attempts = (1..3).map { attemptIndex ->
			val isPositive = Random.nextBoolean()
			CupAttempt(attempt = attemptIndex, isPositive = isPositive)
		}
		val hasPositive = attempts.any { it.isPositive }
		val hasNegative = attempts.any { !it.isPositive }
		CupResultResponse(attempts = attempts, isValid = hasPositive && hasNegative)
	}

	override suspend fun chat(fortuneId: Int, question: String): Result<ChatResponse> = runCatching {
		tryLoadFromAssetsOnce()
		val trimmed = question.trim()
		val fortune = fortunes.firstOrNull { it.id == fortuneId }
		val title = fortune?.title ?: "第${fortuneId}籤"
		val content = fortune?.content ?: ""
		// 解析富含結構的提示：既有對話 / 籤文依據 / 問題
		val ctx = Regex("【籤文依據】([\\s\\S]*?)【", RegexOption.MULTILINE).find(trimmed)?.groupValues?.getOrNull(1)?.trim()
			?: Regex("【籤文依據】([\\s\\S]*)$", RegexOption.MULTILINE).find(trimmed)?.groupValues?.getOrNull(1)?.trim()
		val ques = Regex("【問題】([\\s\\S]*)$", RegexOption.MULTILINE).find(trimmed)?.groupValues?.getOrNull(1)?.trim() ?: trimmed
		val seed = (ques.hashCode() xor fortuneId).toLong()
		val r = kotlin.random.Random(seed)
		val tone = listOf("審慎進取","穩中求進","先難後易","以德化之")[r.nextInt(4)]
		val keyTips = listOf(
			"先處理核心風險，再擬定兩步計劃（當月/季度）",
			"與關鍵人保持溝通，避免單線決策",
			"善用現有資源與強項，少做顛覆式變動",
			"每週檢視進度，微調節奏與優先順序"
		).shuffled(r).take(3)
		val ctxPreview = (ctx?.lines() ?: content.lines()).take(2).joinToString("\n").ifBlank { content.lines().take(2).joinToString("\n") }
		val answer = buildString {
			append("【解讀依據】\n").append(title).append('\n').append(ctxPreview).append("\n\n")
			append("【你的問題】").append(ques.take(200)).append("\n\n")
			append("【綜合解讀（").append(tone).append("）】\n")
			append("此籤意提示：持正念、順勢而為。於當前階段，宜先穩住基本盤，再逐步拓展。\n\n")
			append("【行動建議】\n")
			keyTips.forEachIndexed { i, s -> append(i+1).append(") ").append(s).append('\n') }
			append("\n【結語】以善意與耐心累積小勝，可轉動更大的局勢。")
		}.let { if (it.length > 600) it.take(600) else it }
		ChatResponse(messages = listOf(
			ChatMessage(role = "user", content = ques.take(500)),
			ChatMessage(role = "assistant", content = answer)
		))
	}
}
