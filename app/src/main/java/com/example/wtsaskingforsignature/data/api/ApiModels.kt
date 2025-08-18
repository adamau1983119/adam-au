package com.example.wtsaskingforsignature.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DrawResponse(
	@Json(name = "id") val id: Int, // 1..100
	@Json(name = "title") val title: String? = null,
	@Json(name = "summary") val summary: String? = null,
	@Json(name = "content") val content: String? = null
)

@JsonClass(generateAdapter = true)
data class CupAttempt(
	@Json(name = "attempt") val attempt: Int, // 1..3
	@Json(name = "isPositive") val isPositive: Boolean
)

@JsonClass(generateAdapter = true)
data class CupResultResponse(
	@Json(name = "attempts") val attempts: List<CupAttempt>,
	@Json(name = "isValid") val isValid: Boolean
)

@JsonClass(generateAdapter = true)
data class ChatRequest(
	@Json(name = "fortuneId") val fortuneId: Int,
	@Json(name = "question") val question: String
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
	@Json(name = "role") val role: String,
	@Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
	@Json(name = "messages") val messages: List<ChatMessage>
)
