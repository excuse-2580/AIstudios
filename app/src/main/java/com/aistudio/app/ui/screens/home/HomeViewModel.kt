package com.aistudio.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.data.repository.ChatRepository
import com.aistudio.app.domain.model.Agent
import com.aistudio.app.domain.model.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentAgents: List<Agent> = emptyList(),
    val recentChats: List<Chat> = emptyList(),
    val stats: HomeStats = HomeStats()
)

data class HomeStats(
    val agentCount: Int = 0,
    val chatCount: Int = 0,
    val groupCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val agentRepository: AgentRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Load recent agents
            agentRepository.getAllAgents().collect { agents ->
                _uiState.update { it.copy(recentAgents = agents) }
            }
        }
        
        viewModelScope.launch {
            // Load recent chats
            chatRepository.getAllChats().collect { chats ->
                _uiState.update { it.copy(recentChats = chats) }
            }
        }
        
        viewModelScope.launch {
            // Load stats
            val agentCount = agentRepository.getAgentCount()
            _uiState.update { 
                it.copy(stats = HomeStats(agentCount = agentCount))
            }
        }
    }
}
