package com.aistudio.app.ui.screens.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.data.repository.ChatRepository
import com.aistudio.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

data class ChatRoomUiState(
    val chat: Chat? = null,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val editingMessageId: String? = null,
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val isConnected: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val agentRepository: AgentRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatRoomUiState())
    val uiState: StateFlow<ChatRoomUiState> = _uiState.asStateFlow()
    
    private var currentChatId: String? = null
    private var currentAgentId: String? = null
    
    fun loadChat(chatId: String) {
        currentChatId = chatId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val chat = chatRepository.getChatById(chatId)
            currentAgentId = chat?.participants?.firstOrNull()?.id
            _uiState.update { it.copy(chat = chat, isLoading = false) }
            
            chatRepository.getMessages(chatId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }
    
    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }
    
    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        
        val chatId = currentChatId ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, inputText = "") }
            
            // Create user message
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = "user",
                senderType = SenderType.USER,
                content = text,
                status = MessageStatus.SENDING
            )
            chatRepository.sendMessage(userMessage)
            
            // Update to sent
            chatRepository.updateMessage(userMessage.copy(status = MessageStatus.SENT))
            
            // Call AI
            callAI(chatId, text)
            
            _uiState.update { it.copy(isSending = false) }
        }
    }
    
    fun startEditMessage(messageId: String, content: String) {
        _uiState.update { it.copy(editingMessageId = messageId, inputText = content) }
    }
    
    fun confirmEdit() {
        val messageId = _uiState.value.editingMessageId ?: return
        val newContent = _uiState.value.inputText.trim()
        if (newContent.isBlank()) return
        
        viewModelScope.launch {
            val message = _uiState.value.messages.find { it.id == messageId }
            message?.let {
                chatRepository.updateMessage(it.copy(content = newContent))
            }
            _uiState.update { it.copy(editingMessageId = null, inputText = "") }
        }
    }
    
    fun cancelEdit() {
        _uiState.update { it.copy(editingMessageId = null, inputText = "") }
    }
    
    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            chatRepository.deleteMessage(messageId)
        }
    }
    
    fun copyMessage(content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("message", content)
        clipboard.setPrimaryClip(clip)
    }
    
    fun clearMessages() {
        viewModelScope.launch {
            currentChatId?.let { chatId ->
                _uiState.value.messages.forEach { message ->
                    chatRepository.deleteMessage(message.id)
                }
            }
        }
    }
    
    private suspend fun callAI(chatId: String, userMessage: String) {
        try {
            val agent = currentAgentId?.let { agentRepository.getAgentByIdOnce(it) }
            
            // 调用 AI API（这里暂时用模拟响应）
            val aiResponse = generateAIResponse(agent, userMessage)
            
            val aiMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = currentAgentId ?: "agent",
                senderType = SenderType.AGENT,
                content = aiResponse,
                status = MessageStatus.SENT
            )
            chatRepository.sendMessage(aiMessage)
            
            // 标记用户消息为已读
            chatRepository.markMessagesAsRead(chatId)
            
        } catch (e: Exception) {
            // AI 回复失败
            val errorMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = currentAgentId ?: "agent",
                senderType = SenderType.AGENT,
                content = "抱歉，发生了错误：${e.message}",
                status = MessageStatus.SENT
            )
            chatRepository.sendMessage(errorMessage)
        }
    }
    
    private fun generateAIResponse(agent: Agent?, userMessage: String): String {
        // TODO: 这里应该调用真实的 AI API
        // 目前是占位实现
        val name = agent?.name ?: "AI助手"
        val personality = agent?.personality ?: "智能"
        
        return when {
            userMessage.contains("你好", ignoreCase = true) ||
            userMessage.contains("hi", ignoreCase = true) ||
            userMessage.contains("hello", ignoreCase = true) -> {
                "你好！我是 $name，$personality。有什么我可以帮你的吗？"
            }
            userMessage.contains("你是谁", ignoreCase = true) -> {
                "我是 $name！$personality，专长领域包括：${agent?.expertise?.joinToString("、") ?: "各种任务"}"
            }
            userMessage.contains("帮助", ignoreCase = true) ||
            userMessage.contains("help", ignoreCase = true) -> {
                "我可以帮你：\n• 回答问题\n• 写作创作\n• 编程协助\n• 翻译语言\n• 等等...\n\n有什么需要尽管问我！"
            }
            else -> {
                "收到你的消息：\"$userMessage\"\n\n这只是模拟回复。要启用真实的AI能力，请配置 OpenAI 或 Claude API。"
            }
        }
    }
}
