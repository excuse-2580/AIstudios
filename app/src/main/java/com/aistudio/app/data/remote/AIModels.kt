package com.aistudio.app.data.remote

import com.aistudio.app.domain.model.Agent

/**
 * AI 模型类型
 */
enum class AIModelType {
    OPENAI,
    CLAUDE,
    LOCAL
}

/**
 * 聊天消息请求
 */
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048
)

/**
 * 单条聊天消息
 */
data class ChatMessage(
    val role: String,  // "user", "assistant", "system"
    val content: String
)

/**
 * 聊天响应
 */
data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

/**
 * 选择项
 */
data class Choice(
    val message: ResponseMessage,
    val finishReason: String?
)

/**
 * 响应消息
 */
data class ResponseMessage(
    val role: String?,
    val content: String?
)

/**
 * Token 使用量
 */
data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

/**
 * Claude 特定请求
 */
data class ClaudeRequest(
    val model: String,
    val messages: List<ClaudeMessage>,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 4096
)

/**
 * Claude 消息格式
 */
data class ClaudeMessage(
    val role: String,
    val content: String
)

/**
 * Claude 响应
 */
data class ClaudeResponse(
    val id: String,
    val content: List<ClaudeContent>,
    val usage: ClaudeUsage
)

/**
 * Claude 内容块
 */
data class ClaudeContent(
    val type: String,
    val text: String?
)

/**
 * Claude 使用量
 */
data class ClaudeUsage(
    val inputTokens: Int,
    val outputTokens: Int
)
