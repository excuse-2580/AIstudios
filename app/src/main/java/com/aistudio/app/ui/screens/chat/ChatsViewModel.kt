package com.aistudio.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.data.repository.ChatRepository
import com.aistudio.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatsUiState(
    val chats: List<Chat> = emptyList(),
    val agents: List<Agent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatsUiState())
    val uiState: StateFlow<ChatsUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            chatRepository.getAllChats().collect { chats ->
                _uiState.update { it.copy(chats = chats, isLoading = false) }
            }
        }
        
        viewModelScope.launch {
            agentRepository.getAllAgents().collect { agents ->
                _uiState.update { it.copy(agents = agents) }
            }
        }
    }
    
    fun createChat(agentId: String, chatName: String) {
        viewModelScope.launch {
            val chat = Chat(
                id = UUID.randomUUID().toString(),
                type = ChatType.AGENT_CHAT,
                name = chatName,
                participants = listOf(
                    Participant(
                        id = agentId,
                        name = chatName,
                        role = ParticipantRole.MEMBER
                    )
                )
            )
            chatRepository.createChat(chat)
        }
    }
    
    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            chatRepository.deleteChat(chatId)
        }
    }
}
