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

data class AgentCreateUiState(
    val name: String = "",
    val description: String = "",
    val avatarUrl: String = "",
    val personality: String = "",
    val expertise: List<String> = listOf(""),
    val voiceType: VoiceType = VoiceType.FEMALE_WARM,
    val systemPrompt: String = "",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2000,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AgentCreateViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AgentCreateUiState())
    val uiState: StateFlow<AgentCreateUiState> = _uiState.asStateFlow()
    
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updateAvatarUrl(avatarUrl: String) {
        _uiState.update { it.copy(avatarUrl = avatarUrl) }
    }
    
    fun updatePersonality(personality: String) {
        _uiState.update { it.copy(personality = personality) }
    }
    
    fun addExpertise() {
        _uiState.update { it.copy(expertise = it.expertise + "") }
    }
    
    fun removeExpertise(index: Int) {
        _uiState.update { it.copy(expertise = it.expertise.toMutableList().apply { removeAt(index) }) }
    }
    
    fun updateExpertise(index: Int, value: String) {
        _uiState.update {
            val list = it.expertise.toMutableList()
            list[index] = value
            it.copy(expertise = list)
        }
    }
    
    fun updateVoiceType(voiceType: VoiceType) {
        _uiState.update { it.copy(voiceType = voiceType) }
    }
    
    fun updateSystemPrompt(systemPrompt: String) {
        _uiState.update { it.copy(systemPrompt = systemPrompt) }
    }
    
    fun updateTemperature(temperature: Float) {
        _uiState.update { it.copy(temperature = temperature) }
    }
    
    fun updateMaxTokens(maxTokens: Int) {
        _uiState.update { it.copy(maxTokens = maxTokens) }
    }
    
    fun saveAgent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val state = _uiState.value
                val agent = Agent(
                    name = state.name,
                    description = state.description,
                    avatarUrl = state.avatarUrl,
                    personality = state.personality,
                    expertise = state.expertise.filter { it.isNotBlank() },
                    voiceType = state.voiceType,
                    systemPrompt = state.systemPrompt,
                    temperature = state.temperature,
                    maxTokens = state.maxTokens
                )
                agentRepository.createAgent(agent)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
