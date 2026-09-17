package com.aistudio.app.ui.screens.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.data.repository.GroupRepository
import com.aistudio.app.domain.model.Agent
import com.aistudio.app.domain.model.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class GroupCreateUiState(
    val name: String = "",
    val description: String = "",
    val availableAgents: List<Agent> = emptyList(),
    val selectedAgentIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GroupCreateViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GroupCreateUiState())
    val uiState: StateFlow<GroupCreateUiState> = _uiState.asStateFlow()
    
    init {
        loadAgents()
    }
    
    fun loadAgents() {
        viewModelScope.launch {
            agentRepository.getAllAgents().collect { agents ->
                _uiState.update { it.copy(availableAgents = agents) }
            }
        }
    }
    
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun toggleAgent(agentId: String) {
        _uiState.update {
            val newSet = it.selectedAgentIds.toMutableSet()
            if (agentId in newSet) {
                newSet.remove(agentId)
            } else {
                newSet.add(agentId)
            }
            it.copy(selectedAgentIds = newSet)
        }
    }
    
    fun createGroup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val state = _uiState.value
                val group = Group(
                    id = UUID.randomUUID().toString(),
                    name = state.name,
                    description = state.description,
                    agentIds = state.selectedAgentIds.toList(),
                    memberIds = emptyList(),
                    createdBy = "user"
                )
                groupRepository.createGroup(group)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
