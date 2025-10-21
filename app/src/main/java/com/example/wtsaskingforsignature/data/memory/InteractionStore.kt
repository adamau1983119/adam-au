package com.example.wtsaskingforsignature.data.memory

import com.example.wtsaskingforsignature.WtsApp
import com.example.wtsaskingforsignature.util.WtsLogger
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 本地互動紀錄（JSONL）輕量存儲
 * - 每行一筆 JSON，便於後續匯出/分析
 * - 僅保存必要匿名欄位；不得寫入敏感個資
 */
object InteractionStore {

    private const val FILE_NAME = "interactions.jsonl"

    private fun getStoreFile(): File {
        val dir = WtsApp.instance.filesDir
        return File(dir, FILE_NAME)
    }

    @Synchronized
    fun saveInteraction(
        fortuneId: Int,
        question: String,
        aiResponse: String,
        confidence: Float? = null,
        rating: Int? = null,
        extras: Map<String, String>? = null
    ) {
        try {
            val ts = System.currentTimeMillis()
            val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).format(Date(ts))
            val safeQuestion = question.replace("\n", " ").take(500)
            val safeResponse = aiResponse.replace("\n", " ").take(1000)

            // 手寫 JSON，避免引入額外依賴
            val extrasJson = extras?.entries?.joinToString(
                prefix = "{",
                postfix = "}",
                separator = ","
            ) { (k, v) ->
                "\"" + escape(k) + "\":\"" + escape(v) + "\""
            } ?: "{}"

            val json = StringBuilder()
                .append('{')
                .append("\"ts\":").append(ts).append(',')
                .append("\"time\":\"").append(escape(iso)).append("\",")
                .append("\"fortuneId\":").append(fortuneId).append(',')
                .append("\"question\":\"").append(escape(safeQuestion)).append("\",")
                .append("\"response\":\"").append(escape(safeResponse)).append("\",")
                .append("\"confidence\":").append(confidence?.toString() ?: "null").append(',')
                .append("\"rating\":").append(rating?.toString() ?: "null").append(',')
                .append("\"extras\":").append(extrasJson)
                .append('}')
                .append('\n')
                .toString()

            getStoreFile().appendText(json, Charsets.UTF_8)
            WtsLogger.i("InteractionStore: saved (fortuneId=$fortuneId, rating=${rating ?: "-"}, conf=${confidence ?: -1f}})")
        } catch (e: Exception) {
            WtsLogger.e("InteractionStore save failed: ${e.message}", e)
        }
    }

    private fun escape(s: String): String = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\r", " ")
        .replace("\n", " ")
}


