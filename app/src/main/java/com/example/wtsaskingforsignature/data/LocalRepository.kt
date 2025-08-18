package com.example.wtsaskingforsignature.data

import com.example.wtsaskingforsignature.WtsApp
import com.example.wtsaskingforsignature.data.api.ChatMessage
import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.data.api.CupAttempt
import com.example.wtsaskingforsignature.data.api.CupResultResponse
import com.example.wtsaskingforsignature.data.api.DrawResponse
import com.example.wtsaskingforsignature.util.WtsLogger
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
			val assetName = "fortunes_source.csv"
			val assetList = context.assets.list("")?.toList() ?: emptyList()
			if (!assetList.contains(assetName)) {
				WtsLogger.i("CSV not found: $assetName, skip")
				return false
			}
			context.assets.open(assetName).use { input ->
						val bytes = input.readBytes()
						val text = decodeWithBestCharset(bytes) ?: return false
						BufferedReader(StringReader(text)).use { br ->
							// 逐行讀取，但會把位於引號中的換行合併為同一筆記錄
							val lines = readCsvLogicalLines(br)
							if (lines.isEmpty()) return false
							// 去除 UTF-8 BOM，以免表頭第一個欄位帶有不可見字元
							val header = lines.first().removePrefix("\uFEFF").trim()
					val expected = listOf("id","title","summary","content")
					val headerCells = splitCsvLine(header)
					if (headerCells.map { it.lowercase() } != expected) {
						WtsLogger.w("CSV header mismatch: $header")
					}
					val result = mutableListOf<DrawResponse>()
					val errors = mutableListOf<String>()
							lines.drop(1).forEachIndexed { idx, raw ->
						if (raw.isBlank()) return@forEachIndexed
						val cols = splitCsvLine(raw)
						if (cols.size < 4) {
							errors += "line ${idx+2}: column < 4"
							return@forEachIndexed
						}
						val id = cols[0].toIntOrNull()
						val title = cols[1].trim()
						val summary = cols[2].trim()
						val content = cols[3].trim()
						if (id == null || id !in 1..100) {
							errors += "line ${idx+2}: invalid id=${cols[0]}"
							return@forEachIndexed
						}
						if (title.isBlank() || summary.isBlank() || content.isBlank()) {
							errors += "line ${idx+2}: empty field"
							return@forEachIndexed
						}
						result += DrawResponse(id = id, title = title, summary = summary, content = content)
					}
					if (result.isNotEmpty()) {
						// 以 id 升序排序並覆蓋
						fortunes = result.sortedBy { it.id }
						WtsLogger.i("CSV loaded ${result.size} items, errors=${errors.size}")
						if (errors.isNotEmpty()) {
							errors.take(5).forEach { WtsLogger.w(it) }
						}
						return true
					}
				}
			}
			false
		} catch (t: Throwable) {
			WtsLogger.e("CSV load exception: ${t.message}")
			false
		}
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
		try {
			val context = WtsApp.instance
			context.assets.open("fortunes.json").use { input ->
				BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).use { br ->
					val json = br.readText()
					val moshi = Moshi.Builder()
						.addLast(KotlinJsonAdapterFactory())
						.build()
					val type = Types.newParameterizedType(List::class.java, DrawResponse::class.java)
					val adapter = moshi.adapter<List<DrawResponse>>(type)
					val parsed = adapter.fromJson(json)
					if (!parsed.isNullOrEmpty()) {
						fortunes = parsed
						WtsLogger.i("JSON loaded ${parsed.size} items")
					}
				}
			}
		} catch (_: Throwable) {
			// ignore, keep default
		}
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
					if (inQuotes) sb.append(ch) else { result += sb.toString(); sb.clear() }
				}
				else -> sb.append(ch)
			}
			i++
		}
		result += sb.toString()
		return result
	}

	override suspend fun draw(): Result<DrawResponse> = runCatching {
		tryLoadFromAssetsOnce()
		val id = DrawEngine.draw(1, 100)
		fortunes.first { it.id == id }
	}

	override suspend fun fortune(id: Int): Result<DrawResponse> = runCatching {
		tryLoadFromAssetsOnce()
		fortunes.first { it.id == id }
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
