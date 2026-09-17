package com.aistudio.app.ui.screens.agent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aistudio.app.domain.model.VoiceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentCreateScreen(
    navController: NavController,
    viewModel: AgentCreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("创建智能体", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Info Section
            item {
                Text(
                    text = "基本信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text("智能体名称 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                )
            }
            
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::updateDescription,
                    label = { Text("智能体描述") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }
                )
            }
            
            item {
                OutlinedTextField(
                    value = uiState.avatarUrl,
                    onValueChange = viewModel::updateAvatarUrl,
                    label = { Text("头像URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) }
                )
            }
            
            // Personality Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "性格设定",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                OutlinedTextField(
                    value = uiState.personality,
                    onValueChange = viewModel::updatePersonality,
                    label = { Text("性格特点") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("例如：活泼开朗、善于倾听、喜欢帮助人...") },
                    leadingIcon = { Icon(Icons.Default.Psychology, contentDescription = null) }
                )
            }
            
            // Expertise Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "专长领域",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = viewModel::addExpertise) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("添加")
                    }
                }
            }
            
            items(uiState.expertise) { expertise ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = expertise,
                        onValueChange = { viewModel.updateExpertise(uiState.expertise.indexOf(expertise), it) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("专长领域") }
                    )
                    IconButton(onClick = { viewModel.removeExpertise(uiState.expertise.indexOf(expertise)) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Voice Settings
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "语音设置",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = uiState.voiceType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("音色选择") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        VoiceType.entries.forEach { voiceType ->
                            DropdownMenuItem(
                                text = { Text(voiceType.displayName) },
                                onClick = {
                                    viewModel.updateVoiceType(voiceType)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // AI Settings
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AI参数",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                Text(
                    text = "温度: ${uiState.temperature}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = uiState.temperature,
                    onValueChange = viewModel::updateTemperature,
                    valueRange = 0f..1f,
                    steps = 9
                )
            }
            
            item {
                OutlinedTextField(
                    value = uiState.maxTokens.toString(),
                    onValueChange = { viewModel.updateMaxTokens(it.toIntOrNull() ?: 2000) },
                    label = { Text("最大回复长度") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null) }
                )
            }
            
            item {
                OutlinedTextField(
                    value = uiState.systemPrompt,
                    onValueChange = viewModel::updateSystemPrompt,
                    label = { Text("系统提示词") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8,
                    placeholder = { Text("设定智能体的角色、行为规范...") }
                )
            }
            
            // Save Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = viewModel::saveAgent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = uiState.name.isNotBlank() && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("创建智能体", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
