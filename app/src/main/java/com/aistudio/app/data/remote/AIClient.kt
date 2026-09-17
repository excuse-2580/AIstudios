package com.aistudio.app.data.remote

import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI 客户端
 * 统一管理 OpenAI 和 Claude API 调用
 */
@Singleton
class AIClient @Inject constructor(
    private val openAIApi: OpenAIApi,
    private val claudeApi: ClaudeApi
) {
    
    /**
     * 调用 OpenAI API
     */
    suspend fun callOpenAI(
        apiKey: String,
        request: ChatRequest
    ): Result<ChatResponse> {
        return try {
            val response = openAIApi.chat("Bearer $apiKey", request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("响应体为空"))
            } else {
                Result.failure(Exception("API 错误: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 调用 Claude API
     */
    suspend fun callClaude(
        apiKey: String,
        request: ClaudeRequest
    ): Result<ClaudeResponse> {
        return try {
            val response = claudeApi.chat(apiKey, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("响应体为空"))
            } else {
                Result.failure(Exception("API 错误: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 测试 API 连接
     */
    suspend fun testOpenAI(apiKey: String): Boolean {
        val request = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(ChatMessage("user", "Hi")),
            temperature = 0.7f,
            maxTokens = 10
        )
        return callOpenAI(apiKey, request).isSuccess
    }
    
    suspend fun testClaude(apiKey: String): Boolean {
        val request = ClaudeRequest(
            model = "claude-3-haiku-20240307",
            messages = listOf(ClaudeMessage("user", "Hi")),
            temperature = 0.7f,
            maxTokens = 10
        )
        return callClaude(apiKey, request).isSuccess
    }
}
