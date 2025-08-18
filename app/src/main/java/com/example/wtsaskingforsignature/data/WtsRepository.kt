package com.example.wtsaskingforsignature.data

import com.example.wtsaskingforsignature.data.api.ChatRequest
import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.data.api.CupResultResponse
import com.example.wtsaskingforsignature.data.api.DrawResponse
import com.example.wtsaskingforsignature.data.api.WtsApi

class WtsRepository(private val api: WtsApi) : Repository {
	override suspend fun draw(): Result<DrawResponse> = runCatching { api.draw() }
	override suspend fun fortune(id: Int): Result<DrawResponse> = runCatching { api.getFortune(id) }
	override suspend fun cupResult(): Result<CupResultResponse> = runCatching { api.cupResult() }
	override suspend fun chat(fortuneId: Int, question: String): Result<ChatResponse> = runCatching {
		api.chat(ChatRequest(fortuneId = fortuneId, question = question))
	}
}
