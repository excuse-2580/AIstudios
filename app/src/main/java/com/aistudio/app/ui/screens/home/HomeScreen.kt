package com.aistudio.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aistudio.app.ui.components.AgentCard
import com.aistudio.app.ui.components.QuickActionCard
import com.aistudio.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("AI智能体工坊", fontWeight = FontWeight.Bold) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            // Quick Actions
            item {
                Text(
                    text = "快捷操作",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionCard(
                            icon = Icons.Default.Add,
                            title = "创建智能体",
                            subtitle = "打造专属AI伙伴",
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { navController.navigate(Screen.AgentCreate.route) }
                        )
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.Chat,
                            title = "开始对话",
                            subtitle = "与AI智能体聊天",
                            color = MaterialTheme.colorScheme.secondary,
                            onClick = { navController.navigate(Screen.Chats.route) }
                        )
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.Groups,
                            title = "创建群聊",
                            subtitle = "多智能体协作",
                            color = MaterialTheme.colorScheme.tertiary,
                            onClick = { navController.navigate(Screen.GroupCreate.route) }
                        )
                    }
                }
            }
            
            // Recent Agents
            if (uiState.recentAgents.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "最近的智能体",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { navController.navigate(Screen.Agents.route) }) {
                            Text("查看全部")
                        }
                    }
                }
                
                items(uiState.recentAgents.take(3)) { agent ->
                    AgentCard(
                        agent = agent,
                        onClick = { 
                            navController.navigate(Screen.AgentDetail.createRoute(agent.id)) 
                        }
                    )
                }
            }
            
            // Recent Chats
            if (uiState.recentChats.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "最近的对话",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { navController.navigate(Screen.Chats.route) }) {
                            Text("查看全部")
                        }
                    }
                }
                
                items(uiState.recentChats.take(3)) { chat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate(Screen.ChatRoom.createRoute(chat.id)) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (chat.type.name == "GROUP") Icons.Default.Groups else Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = chat.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                chat.lastMessage?.let {
                                    Text(
                                        text = it.content.take(30),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (chat.unreadCount > 0) {
                                Badge { Text(chat.unreadCount.toString()) }
                            }
                        }
                    }
                }
            }
            
            // Statistics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "我的数据",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatItem(label = "智能体", value = uiState.stats.agentCount.toString())
                            StatItem(label = "对话", value = uiState.stats.chatCount.toString())
                            StatItem(label = "群聊", value = uiState.stats.groupCount.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
