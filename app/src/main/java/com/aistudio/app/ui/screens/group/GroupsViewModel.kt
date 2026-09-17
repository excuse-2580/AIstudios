package com.aistudio.app.ui.screens.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.app.data.repository.GroupRepository
import com.aistudio.app.domain.model.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupsUiState(
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()
    
    init {
        loadGroups()
    }
    
    private fun loadGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            groupRepository.getAllGroups().collect { groups ->
                _uiState.update { it.copy(groups = groups, isLoading = false) }
            }
        }
    }
}
