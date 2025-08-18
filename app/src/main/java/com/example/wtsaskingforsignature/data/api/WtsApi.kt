package com.example.wtsaskingforsignature.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WtsApi {
	// 直接抽籤 / 每日一籤：後端回傳 1..100 其中之一與摘要/全文
	@GET("draw")
	suspend fun draw(): DrawResponse

	// 指定籤文內容
	@GET("fortunes/{id}")
	suspend fun getFortune(@Path("id") id: Int): DrawResponse

	// 擲杯：後端完成三次擲杯並回傳結果與是否有效
	@GET("cup/result")
	suspend fun cupResult(): CupResultResponse

	// 對話：傳入籤文 id 與問題，回傳多輪訊息
	@POST("chat")
	suspend fun chat(@Body request: ChatRequest): ChatResponse
}
