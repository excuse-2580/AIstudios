package com.aistudio.app.data.remote

import com.aistudio.app.domain.model.Agent

/**
 * API 配置管理器
 */
object ApiConfig {
    
    // 默认配置
    private var openAIApiKey: String = ""
    private var claudeApiKey: String = ""
    private var baseUrl: String = "https://api.openai.com/"
    private var claudeBaseUrl: String = "https://api.anthropic.com/"
    
    // 默认模型
    private var defaultModel: AIModelType = AIModelType.OPENAI
    
    fun configureOpenAI(apiKey: String, baseUrl: String? = null) {
        this.openAIApiKey = apiKey
        baseUrl?.let { this.baseUrl = it }
    }
    
    fun configureClaude(apiKey: String, baseUrl: String? = null) {
        this.claudeApiKey = apiKey
        baseUrl?.let { this.claudeBaseUrl = it }
    }
    
    fun setDefaultModel(model: AIModelType) {
        this.defaultModel = model
    }
    
    fun getOpenAIApiKey(): String = openAIApiKey
    fun getClaudeApiKey(): String = claudeApiKey
    fun getOpenAIBaseUrl(): String = baseUrl
    fun getClaudeBaseUrl(): String = claudeBaseUrl
    fun getDefaultModel(): AIModelType = defaultModel
    
    fun isConfigured(): Boolean = openAIApiKey.isNotEmpty() || claudeApiKey.isNotEmpty()
    
    fun clear() {
        openAIApiKey = ""
        claudeApiKey = ""
        defaultModel = AIModelType.OPENAI
    }
}

/**
 * API Key 输入/配置界面数据
 */
data class ApiConfigState(
    val openAIKey: String = "",
    val claudeKey: String = "",
    val selectedModel: AIModelType = AIModelType.OPENAI,
    val isConfigured: Boolean = false,
    val isTesting: Boolean = false,
    val testResult: String? = null
)
