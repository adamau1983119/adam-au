package com.example.wtsaskingforsignature.ui.chat

import android.content.Context
import com.example.wtsaskingforsignature.data.ServiceLocator
import com.example.wtsaskingforsignature.data.api.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface ChatCallback {
    fun onSuccess(messages: List<ChatMessage>)
    fun onError(message: String)
}

object ChatApiBridge {
    @JvmStatic
    fun chat(context: Context, fortuneId: Int, question: String, callback: ChatCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = ServiceLocator.repository.chat(fortuneId, question)
            withContext(Dispatchers.Main) {
                res.onSuccess { resp ->
                    callback.onSuccess(resp.messages)
                }.onFailure { e ->
                    callback.onError(e.message ?: "Unknown error")
                }
            }
        }
    }
}


