package com.aistudio.app.ui.screens.agent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aistudio.app.ui.components.AgentCard
import com.aistudio.app.ui.components.EmptyStateView
import com.aistudio.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsScreen(
    navController: NavController,
    viewModel: AgentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的智能体", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AgentCreate.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "创建智能体")
            }
        }
    ) { padding ->
        if (uiState.agents.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SmartToy,
                title = "还没有智能体",
                subtitle = "创建一个属于你的AI智能体吧！",
                actionLabel = "立即创建",
                onAction = { navController.navigate(Screen.AgentCreate.route) },
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.agents, key = { it.id }) { agent ->
                    AgentCard(
                        agent = agent,
                        onClick = { navController.navigate(Screen.AgentDetail.createRoute(agent.id)) }
                    )
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { agentId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个智能体吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAgent(agentId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}
