package com.aistudio.app.ui.screens.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.domain.model.Agent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgentDetailUiState(
    val agent: Agent? = null,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AgentDetailViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AgentDetailUiState())
    val uiState: StateFlow<AgentDetailUiState> = _uiState.asStateFlow()
    
    private var currentAgentId: String? = null
    
    fun loadAgent(agentId: String) {
        currentAgentId = agentId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            agentRepository.getAgentById(agentId).collect { agent ->
                _uiState.update { it.copy(agent = agent, isLoading = false) }
            }
        }
    }
    
    fun deleteAgent() {
        viewModelScope.launch {
            try {
                currentAgentId?.let { agentRepository.deleteAgent(it) }
                _uiState.update { it.copy(isDeleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun improveAgent(hint: String) {
        viewModelScope.launch {
            try {
                val currentAgent = _uiState.value.agent ?: return@launch
                val improvedAgent = currentAgent.copy(
                    personality = if (hint.contains("活泼", ignoreCase = true)) {
                        "${currentAgent.personality}\n特别活泼开朗，善于活跃气氛。".trim()
                    } else currentAgent.personality,
                    updatedAt = System.currentTimeMillis()
                )
                agentRepository.updateAgent(improvedAgent)
                _uiState.update { it.copy(agent = improvedAgent) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
