package com.aistudio.app.ui.screens.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.domain.model.Agent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgentsUiState(
    val agents: List<Agent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AgentsViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AgentsUiState())
    val uiState: StateFlow<AgentsUiState> = _uiState.asStateFlow()
    
    init {
        loadAgents()
    }
    
    private fun loadAgents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            agentRepository.getAllAgents().collect { agents ->
                _uiState.update { 
                    it.copy(agents = agents, isLoading = false) 
                }
            }
        }
    }
    
    fun deleteAgent(agentId: String) {
        viewModelScope.launch {
            try {
                agentRepository.deleteAgent(agentId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
