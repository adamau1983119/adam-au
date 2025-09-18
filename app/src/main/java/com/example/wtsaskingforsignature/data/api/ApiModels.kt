package com.example.wtsaskingforsignature.data.api

data class DrawResponse(
	val id: Int, // 1..100
	val title: String? = null,
	val summary: String? = null,
	val content: String? = null
)

data class CupAttempt(
	val attempt: Int, // 1..3
	val isPositive: Boolean
)

data class CupResultResponse(
	val attempts: List<CupAttempt>,
	val isValid: Boolean
)

data class ChatRequest(
	val fortuneId: Int,
	val question: String
)

data class ChatMessage(
	val role: String,
	val content: String
)

data class ChatResponse(
	val messages: List<ChatMessage>
)
