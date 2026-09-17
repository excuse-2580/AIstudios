package com.aistudio.app.ui.screens.agent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aistudio.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDetailScreen(
    agentId: String,
    navController: NavController,
    viewModel: AgentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showImproveDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(agentId) {
        viewModel.loadAgent(agentId)
    }
    
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("智能体详情", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AgentEdit.createRoute(agentId)) }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
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
        uiState.agent?.let { agent ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                if (agent.avatarUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = agent.avatarUrl,
                                        contentDescription = agent.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = agent.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (agent.description.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = agent.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Action Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { navController.navigate(Screen.ChatRoom.createRoute(agentId)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("对话")
                                }
                                
                                OutlinedButton(
                                    onClick = { showImproveDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("改进")
                                }
                            }
                        }
                    }
                }
                
                // Voice Settings
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VoiceChat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("语音设置", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("音色: ${agent.voiceType.displayName}")
                        }
                    }
                }
                
                // Personality
                if (agent.personality.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("性格设定", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(agent.personality)
                            }
                        }
                    }
                }
                
                // Expertise
                if (agent.expertise.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("专长领域", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(agent.expertise) { skill ->
                                        SuggestionChip(
                                            onClick = { },
                                            label = { Text(skill) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // AI Parameters
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI参数", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("温度: ${agent.temperature}")
                            Text("最大回复长度: ${agent.maxTokens}")
                        }
                    }
                }
                
                // System Prompt
                if (agent.systemPrompt.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("系统提示词", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = agent.systemPrompt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("智能体不存在")
                }
            }
        }
    }
    
    // Delete Dialog
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
    
    // Improve Dialog
    if (showImproveDialog) {
        ImproveAgentDialog(
            onDismiss = { showImproveDialog = false },
            onImprove = { hint ->
                viewModel.improveAgent(hint)
                showImproveDialog = false
            }
        )
    }
}

@Composable
private fun ImproveAgentDialog(
    onDismiss: () -> Unit,
    onImprove: (String) -> Unit
) {
    var hint by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("优化智能体") },
        text = {
            Column {
                Text("描述你想要改进的地方，我会帮你优化智能体的设定。")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = hint,
                    onValueChange = { hint = it },
                    label = { Text("改进建议") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    placeholder = { Text("例如：让智能体更活泼一些，增加更多专业术语...") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onImprove(hint) }, enabled = hint.isNotBlank()) {
                Text("应用改进")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
