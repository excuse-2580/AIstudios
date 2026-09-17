package com.aistudio.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aistudio.app.ui.screens.agent.*
import com.aistudio.app.ui.screens.chat.*
import com.aistudio.app.ui.screens.group.*
import com.aistudio.app.ui.screens.home.HomeScreen
import com.aistudio.app.ui.screens.settings.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // 判断是否显示底部导航
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 主导航页面
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            
            composable(Screen.Agents.route) {
                AgentsScreen(navController = navController)
            }
            
            composable(Screen.Chats.route) {
                ChatsScreen(navController = navController)
            }
            
            composable(Screen.Groups.route) {
                GroupsScreen(navController = navController)
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            
            // 智能体相关
            composable(Screen.AgentCreate.route) {
                AgentCreateScreen(navController = navController)
            }
            
            composable(
                route = Screen.AgentDetail.route,
                arguments = listOf(navArgument("agentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val agentId = backStackEntry.arguments?.getString("agentId") ?: ""
                AgentDetailScreen(agentId = agentId, navController = navController)
            }
            
            composable(
                route = Screen.AgentEdit.route,
                arguments = listOf(navArgument("agentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val agentId = backStackEntry.arguments?.getString("agentId") ?: ""
                AgentEditScreen(agentId = agentId, navController = navController)
            }
            
            // 对话相关
            composable(
                route = Screen.ChatRoom.route,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                ChatRoomScreen(chatId = chatId, navController = navController)
            }
            
            // 群聊相关
            composable(Screen.GroupCreate.route) {
                GroupCreateScreen(navController = navController)
            }
            
            composable(
                route = Screen.GroupDetail.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                GroupDetailScreen(groupId = groupId, navController = navController)
            }
            
            // 通话相关
            composable(
                route = Screen.VoiceCall.route,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                VoiceCallScreen(chatId = chatId, navController = navController)
            }
            
            composable(
                route = Screen.ScreenShare.route,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                ScreenShareScreen(chatId = chatId, navController = navController)
            }
            
            // 设置相关
            composable(Screen.ApiSettings.route) {
                ApiSettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
