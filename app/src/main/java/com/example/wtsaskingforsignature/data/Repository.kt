package com.example.wtsaskingforsignature.data

import com.example.wtsaskingforsignature.data.api.ChatResponse
import com.example.wtsaskingforsignature.data.api.CupResultResponse
import com.example.wtsaskingforsignature.data.api.DrawResponse

interface Repository {
	suspend fun draw(): Result<DrawResponse>
	suspend fun fortune(id: Int): Result<DrawResponse>
	suspend fun cupResult(): Result<CupResultResponse>
	suspend fun chat(fortuneId: Int, question: String): Result<ChatResponse>
}
