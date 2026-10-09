package com.example.composeagent.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // 对应 Spring Boot 后端 TimeController 的 GET /api/time，返回 {"time":"2026-10-08 12:00:00"}
    @GET("api/time")
    suspend fun getCurrentTime(): TimeResponse

    // 对应 Spring Boot 后端 ChatController 的 POST /api/chat
    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}

data class TimeResponse(
    val time: String // 后端已格式化好的时间字符串
)

data class ChatRequest(
    val prompt: String
)

data class ChatResponse(
    val reply: String
)
