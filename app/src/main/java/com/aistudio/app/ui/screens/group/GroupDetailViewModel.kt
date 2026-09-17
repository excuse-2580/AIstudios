package com.aistudio.app.ui.screens.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.GroupRepository
import com.aistudio.app.domain.model.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupDetailUiState(
    val group: Group? = null,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()
    
    private var currentGroupId: String? = null
    
    fun loadGroup(groupId: String) {
        currentGroupId = groupId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            groupRepository.getGroupById(groupId).let { group ->
                _uiState.update { it.copy(group = group, isLoading = false) }
            }
        }
    }
    
    fun deleteGroup() {
        viewModelScope.launch {
            try {
                currentGroupId?.let { groupRepository.deleteGroup(it) }
                _uiState.update { it.copy(isDeleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
