package com.aistudio.app.ui.screens.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.AgentRepository
import com.aistudio.app.domain.model.Agent
import com.aistudio.app.domain.model.VoiceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgentEditUiState(
    val name: String = "",
    val description: String = "",
    val personality: String = "",
    val expertise: String = "",
    val voiceType: VoiceType = VoiceType.NEUTRAL,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val avatarUrl: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AgentEditViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AgentEditUiState())
    val uiState: StateFlow<AgentEditUiState> = _uiState.asStateFlow()
    
    private var currentAgentId: String? = null
    
    fun loadAgent(agentId: String) {
        currentAgentId = agentId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            agentRepository.getAgentById(agentId).let { agent ->
                agent?.let {
                    _uiState.update { state ->
                        state.copy(
                            name = it.name,
                            description = it.description,
                            personality = it.personality,
                            expertise = it.expertise,
                            voiceType = it.voiceType,
                            temperature = it.temperature,
                            maxTokens = it.maxTokens,
                            avatarUrl = it.avatarUrl,
                            isLoading = false
                        )
                    }
                } ?: _uiState.update { it.copy(isLoading = false, error = "智能体不存在") }
            }
        }
    }
    
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updatePersonality(personality: String) {
        _uiState.update { it.copy(personality = personality) }
    }
    
    fun updateExpertise(expertise: String) {
        _uiState.update { it.copy(expertise = expertise) }
    }
    
    fun updateVoiceType(voiceType: VoiceType) {
        _uiState.update { it.copy(voiceType = voiceType) }
    }
    
    fun updateTemperature(temperature: Float) {
        _uiState.update { it.copy(temperature = temperature) }
    }
    
    fun updateMaxTokens(maxTokens: Int) {
        _uiState.update { it.copy(maxTokens = maxTokens.coerceIn(64, 8192)) }
    }
    
    fun saveAgent() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.name.isBlank()) {
                _uiState.update { it.copy(error = "名称不能为空") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            try {
                val agent = Agent(
                    id = currentAgentId ?: return@launch,
                    name = state.name,
                    description = state.description,
                    personality = state.personality,
                    expertise = state.expertise,
                    voiceType = state.voiceType,
                    temperature = state.temperature,
                    maxTokens = state.maxTokens,
                    avatarUrl = state.avatarUrl,
                    createdAt = System.currentTimeMillis()
                )
                agentRepository.updateAgent(agent)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun deleteAgent() {
        viewModelScope.launch {
            try {
                currentAgentId?.let { agentRepository.deleteAgent(it) }
                _uiState.update { it.copy(isDeleted = true, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
