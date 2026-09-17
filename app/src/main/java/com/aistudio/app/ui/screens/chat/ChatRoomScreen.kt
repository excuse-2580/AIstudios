package com.aistudio.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.aistudio.app.domain.model.Message
import com.aistudio.app.domain.model.MessageStatus
import com.aistudio.app.domain.model.SenderType
import com.aistudio.app.ui.navigation.Screen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    chatId: String,
    navController: NavController,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }
    var showMessageMenu by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(chatId) {
        viewModel.loadChat(chatId)
    }
    
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.chat?.avatarUrl?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = uiState.chat?.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.SmartToy,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(uiState.chat?.name ?: "对话", fontWeight = FontWeight.Bold)
                            if (uiState.isConnected) {
                                Text(
                                    "在线",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.VoiceCall.createRoute(chatId)) }) {
                        Icon(Icons.Default.Call, contentDescription = "语音通话")
                    }
                    IconButton(onClick = { navController.navigate(Screen.ScreenShare.createRoute(chatId)) }) {
                        Icon(Icons.Default.Screencast, contentDescription = "屏幕共享")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "更多")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("查看智能体详情") },
                            onClick = { showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("清空对话") },
                            onClick = {
                                viewModel.clearMessages()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            MessageInput(
                value = uiState.inputText,
                onValueChange = viewModel::updateInputText,
                onSend = viewModel::sendMessage,
                onEdit = uiState.editingMessageId?.let { { viewModel.confirmEdit() } },
                onCancelEdit = { viewModel.cancelEdit() },
                isEditing = uiState.editingMessageId != null,
                modifier = Modifier.imePadding()
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    onLongClick = {
                        if (message.senderType == SenderType.USER) {
                            showMessageMenu = message.id
                        }
                    },
                    onEdit = { viewModel.startEditMessage(message.id, message.content) },
                    onDelete = { viewModel.deleteMessage(message.id) },
                    onCopy = { viewModel.copyMessage(message.content) }
                )
            }
        }
    }
    
    // 消息操作菜单
    showMessageMenu?.let { messageId ->
        AlertDialog(
            onDismissRequest = { showMessageMenu = null },
            title = { Text("消息操作") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            uiState.messages.find { it.id == messageId }?.let {
                                viewModel.startEditMessage(messageId, it.content)
                            }
                            showMessageMenu = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("编辑")
                    }
                    TextButton(
                        onClick = {
                            uiState.messages.find { it.id == messageId }?.let {
                                viewModel.copyMessage(it.content)
                            }
                            showMessageMenu = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("复制")
                    }
                    TextButton(
                        onClick = {
                            viewModel.deleteMessage(messageId)
                            showMessageMenu = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("删除")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMessageMenu = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.senderType == SenderType.USER
    val scope = rememberCoroutineScope()
    
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.content,
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        if (isUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = when (message.status) {
                                    MessageStatus.SENDING -> Icons.Default.Schedule
                                    MessageStatus.SENT -> Icons.Default.Done
                                    MessageStatus.DELIVERED -> Icons.Default.DoneAll
                                    MessageStatus.READ -> Icons.Default.DoneAll
                                    MessageStatus.FAILED -> Icons.Default.Error
                                },
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onCancelEdit: () -> Unit = {},
    isEditing: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (isEditing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "编辑消息",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = onCancelEdit,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "取消",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(if (isEditing) "编辑消息..." else "输入消息...") },
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "发送")
                }
            }
        }
    }
}
