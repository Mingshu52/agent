package com.example.composeagent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeagent.model.Message
import com.example.composeagent.network.ChatRequest
import com.example.composeagent.network.RetrofitClient // 引入刚刚写的网络工具
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    val messages = mutableStateListOf<Message>()
    var inputText by mutableStateOf("")

    init {
        messages.add(Message("你好！我是 ComposeAgent，你的智能屏幕助手。你可以让我帮你查日期、截图或闲聊。", false))
    }

    fun sendMessage() {
        if (inputText.isBlank()) return
        val userMsg = inputText.trim()
        messages.add(Message(userMsg, true))
        inputText = ""

        viewModelScope.launch {
            val reply: String

            if (userMsg.contains("截图")) {
                reply = "【系统工具调用】正在为您执行截图操作...\n(这里实际调用 MediaProjection API)"
            } else if (userMsg.contains("日期") || userMsg.contains("今天")) {
                // 🌟 从后端 /api/time 接口获取服务器时间
                reply = try {
                    val response = RetrofitClient.api.getCurrentTime()
                    "【网络API调用】当前真实时间：${response.time}"
                } catch (e: Exception) {
                    "【网络异常】无法获取网络时间，错误信息：${e.message}"
                }
            } else {
                // 🌟 其他消息走大模型对话接口 POST /api/chat
                reply = try {
                    val response = RetrofitClient.api.chat(ChatRequest(userMsg))
                    response.reply
                } catch (e: Exception) {
                    "【网络异常】无法连接AI服务，错误信息：${e.message}"
                }
            }

            messages.add(Message(reply, false))
        }
    }
}