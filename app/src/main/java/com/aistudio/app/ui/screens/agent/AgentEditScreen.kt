package com.aistudio.app.ui.screens.agent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentEditScreen(
    agentId: String,
    navController: NavController,
    viewModel: AgentEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(agentId) {
        viewModel.loadAgent(agentId)
    }
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑智能体", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 名称
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("智能体名称 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
            )
            
            // 描述
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }
            )
            
            // 性格设定
            OutlinedTextField(
                value = uiState.personality,
                onValueChange = viewModel::updatePersonality,
                label = { Text("性格设定") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("例如：活泼开朗、乐于助人、幽默风趣...") },
                leadingIcon = { Icon(Icons.Default.Psychology, contentDescription = null) }
            )
            
            // 系统提示词
            OutlinedTextField(
                value = uiState.systemPrompt,
                onValueChange = viewModel::updateSystemPrompt,
                label = { Text("系统提示词 (可选)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                placeholder = { Text("自定义 AI 的系统设定指令...") },
                leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = null) }
            )
            
            // 专长领域（可多个）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("专长领域", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                TextButton(onClick = viewModel::addExpertise) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("添加")
                }
            }
            uiState.expertise.forEachIndexed { index, exp ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = exp,
                        onValueChange = { viewModel.updateExpertise(index, it) },
                        label = { Text("专长 ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Stars, contentDescription = null) }
                    )
                    IconButton(onClick = { viewModel.removeExpertise(index) }) {
                        Icon(Icons.Default.Remove, contentDescription = "删除")
                    }
                }
            }
            
            Divider()
            
            // AI参数区域
            Text(
                text = "AI 参数设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Temperature
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("温度 (Temperature)", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = String.format("%.1f", uiState.temperature),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = uiState.temperature,
                    onValueChange = viewModel::updateTemperature,
                    valueRange = 0f..2f,
                    steps = 19
                )
                Text(
                    text = "控制随机性：0 保守精准 | 1.0 平衡 | 2.0 创意自由",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Max Tokens
            OutlinedTextField(
                value = uiState.maxTokens.toString(),
                onValueChange = { viewModel.updateMaxTokens(it.toIntOrNull() ?: 2048) },
                label = { Text("最大回复长度 (Max Tokens)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = { Text("单次回复最大token数，建议 512-4096") },
                leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null) }
            )
            
            Divider()
            
            // 语音设置区域
            Text(
                text = "语音设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 语音类型选择
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = getVoiceTypeName(uiState.voiceType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("语音音色") },
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
                            text = { Text(getVoiceTypeName(voiceType)) },
                            onClick = {
                                viewModel.updateVoiceType(voiceType)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 保存按钮
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
                    Text("保存修改", style = MaterialTheme.typography.titleMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // 删除确认弹窗
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个智能体吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAgent()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

private fun getVoiceTypeName(voiceType: com.aistudio.app.domain.model.VoiceType): String {
    return when (voiceType) {
        com.aistudio.app.domain.model.VoiceType.FEMALE_WARM -> "温柔女声"
        com.aistudio.app.domain.model.VoiceType.FEMALE_BRIGHT -> "明亮女声"
        com.aistudio.app.domain.model.VoiceType.MALE_WARM -> "温暖男声"
        com.aistudio.app.domain.model.VoiceType.MALE_DEEP -> "低沉男声"
        com.aistudio.app.domain.model.VoiceType.NEUTRAL -> "中性声"
        com.aistudio.app.domain.model.VoiceType.ROBOTIC -> "机器人声"
    }
}
