@file:OptIn(ExperimentalMaterial3Api::class)

package com.aistudio.app.ui.screens.chat

import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aistudio.app.domain.model.Chat
import com.aistudio.app.domain.model.ChatType
import com.aistudio.app.ui.components.EmptyStateView
import com.aistudio.app.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    navController: NavController,
    viewModel: ChatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("对话", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "新建对话")
            }
        }
    ) { padding ->
        if (uiState.chats.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Chat,
                title = "还没有对话",
                subtitle = "开始和智能体对话吧！",
                actionLabel = "选择智能体",
                onAction = { navController.navigate(Screen.Agents.route) },
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(uiState.chats, key = { it.id }) { chat ->
                    ChatListItem(
                        chat = chat,
                        onClick = { navController.navigate(Screen.ChatRoom.createRoute(chat.id)) }
                    )
                    Divider()
                }
            }
        }
    }
    
    if (showCreateDialog) {
        CreateChatDialog(
            agents = uiState.agents,
            onDismiss = { showCreateDialog = false },
            onCreateChat = { agentId, chatName ->
                viewModel.createChat(agentId, chatName)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(chat.name, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            chat.lastMessage?.let {
                Text(
                    it.content.take(50),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (chat.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = chat.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = if (chat.type == ChatType.GROUP) Icons.Default.Groups else Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                chat.lastMessage?.let {
                    Text(
                        formatTime(it.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Badge { Text(chat.unreadCount.toString()) }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun CreateChatDialog(
    agents: List<com.aistudio.app.domain.model.Agent>,
    onDismiss: () -> Unit,
    onCreateChat: (agentId: String, chatName: String) -> Unit
) {
    var selectedAgent by remember { mutableStateOf<com.aistudio.app.domain.model.Agent?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("开始新对话") },
        text = {
            Column {
                if (agents.isEmpty()) {
                    Text("请先创建智能体")
                } else {
                    agents.forEach { agent ->
                        ListItem(
                            headlineContent = { Text(agent.name) },
                            leadingContent = {
                                Icon(Icons.Default.SmartToy, contentDescription = null)
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = selectedAgent == agent,
                                    onClick = { selectedAgent = agent }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedAgent?.let { onCreateChat(it.id, it.name) } },
                enabled = selectedAgent != null
            ) {
                Text("开始对话")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "刚刚"
        diff < 3600_000 -> "${diff / 60_000}分钟前"
        diff < 86400_000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(timestamp))
    }
}
