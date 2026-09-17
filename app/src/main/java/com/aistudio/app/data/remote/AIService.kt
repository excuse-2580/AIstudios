package com.aistudio.app.data.remote

import com.aistudio.app.domain.model.Agent
import com.aistudio.app.domain.model.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OpenAI API 接口
 */
interface OpenAIApi {
    @POST("v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
}

/**
 * Claude API 接口
 */
interface ClaudeApi {
    @POST("v1/messages")
    suspend fun chat(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Header("anthropic-dangerous-direct-browser-access") direct: String = "true",
        @Body request: ClaudeRequest
    ): Response<ClaudeResponse>
}

/**
 * AI 服务层
 * 统一的 AI 调用接口，支持 OpenAI / Claude
 */
@Singleton
class AIService @Inject constructor(
    private val openAIApi: OpenAIApi,
    private val claudeApi: ClaudeApi
) {
    
    /**
     * 发送消息并获取 AI 回复
     */
    suspend fun sendMessage(
        agent: Agent,
        messages: List<Message>,
        userMessage: String,
        apiKey: String,
        modelType: AIModelType
    ): Result<String> {
        return when (modelType) {
            AIModelType.OPENAI -> callOpenAI(agent, messages, userMessage, apiKey)
            AIModelType.CLAUDE -> callClaude(agent, messages, userMessage, apiKey)
            AIModelType.LOCAL -> Result.failure(Exception("本地模型暂不支持"))
        }
    }
    
    private suspend fun callOpenAI(
        agent: Agent,
        messages: List<Message>,
        userMessage: String,
        apiKey: String
    ): Result<String> {
        return try {
            // 构建系统提示词
            val systemPrompt = buildSystemPrompt(agent)
            
            // 构建消息历史
            val chatMessages = messages.map { msg ->
                ChatMessage(
                    role = when (msg.senderType) {
                        com.aistudio.app.domain.model.SenderType.USER -> "user"
                        com.aistudio.app.domain.model.SenderType.AGENT -> "assistant"
                        com.aistudio.app.domain.model.SenderType.SYSTEM -> "system"
                    },
                    content = msg.content
                )
            }.toMutableList()
            
            // 添加当前用户消息
            chatMessages.add(ChatMessage("user", userMessage))
            
            val request = ChatRequest(
                model = agent.modelName ?: "gpt-3.5-turbo",
                messages = listOf(ChatMessage("system", systemPrompt)) + chatMessages,
                temperature = agent.temperature,
                maxTokens = agent.maxTokens
            )
            
            val response = openAIApi.chat("Bearer $apiKey", request)
            
            if (response.isSuccessful) {
                val body = response.body()
                val content = body?.choices?.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("AI 返回内容为空"))
                }
            } else {
                Result.failure(Exception("API 错误: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun callClaude(
        agent: Agent,
        messages: List<Message>,
        userMessage: String,
        apiKey: String
    ): Result<String> {
        return try {
            val systemPrompt = buildSystemPrompt(agent)
            
            val claudeMessages = messages.map { msg ->
                ClaudeMessage(
                    role = when (msg.senderType) {
                        com.aistudio.app.domain.model.SenderType.USER -> "user"
                        com.aistudio.app.domain.model.SenderType.AGENT -> "assistant"
                        com.aistudio.app.domain.model.SenderType.SYSTEM -> "system"
                    },
                    content = msg.content
                )
            }.toMutableList()
            
            claudeMessages.add(ClaudeMessage("user", userMessage))
            
            val request = ClaudeRequest(
                model = agent.modelName ?: "claude-3-haiku-20240307",
                messages = claudeMessages,
                temperature = agent.temperature,
                maxTokens = agent.maxTokens
            )
            
            val response = claudeApi.chat(apiKey = apiKey, request = request)
            
            if (response.isSuccessful) {
                val body = response.body()
                val text = body?.content?.firstOrNull()?.text
                if (text != null) {
                    Result.success(text)
                } else {
                    Result.failure(Exception("AI 返回内容为空"))
                }
            } else {
                Result.failure(Exception("API 错误: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildSystemPrompt(agent: Agent): String {
        return buildString {
            append("你是一个名为 ${agent.name} 的AI智能体。\n")
            if (agent.personality.isNotEmpty()) {
                append("性格特点：${agent.personality}\n")
            }
            if (agent.expertise.isNotEmpty()) {
                append("专长领域：${agent.expertise.joinToString("、")}\n")
            }
            if (agent.description.isNotEmpty()) {
                append("简介：${agent.description}\n")
            }
            append("\n请根据以上设定，以自然、友好的方式回答用户的问题。")
        }
    }
}
