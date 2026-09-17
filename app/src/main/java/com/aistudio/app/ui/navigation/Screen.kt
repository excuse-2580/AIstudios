package com.aistudio.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // 主导航
    data object Home : Screen("home")
    data object Agents : Screen("agents")
    data object Chats : Screen("chats")
    data object Groups : Screen("groups")
    data object Settings : Screen("settings")
    
    // 智能体相关
    data object AgentCreate : Screen("agent/create")
    data object AgentDetail : Screen("agent/{agentId}") {
        fun createRoute(agentId: String) = "agent/$agentId"
    }
    data object AgentEdit : Screen("agent/{agentId}/edit") {
        fun createRoute(agentId: String) = "agent/$agentId/edit"
    }
    
    // 对话相关
    data object ChatRoom : Screen("chat/{chatId}") {
        fun createRoute(chatId: String) = "chat/$chatId"
    }
    
    // 群聊相关
    data object GroupCreate : Screen("group/create")
    data object GroupDetail : Screen("group/{groupId}") {
        fun createRoute(groupId: String) = "group/$groupId"
    }
    
    // 通话相关
    data object VoiceCall : Screen("call/voice/{chatId}") {
        fun createRoute(chatId: String) = "call/voice/$chatId"
    }
    data object ScreenShare : Screen("call/screen/{chatId}") {
        fun createRoute(chatId: String) = "call/screen/$chatId"
    }
    
    // 设置相关
    data object ApiSettings : Screen("settings/api")
    data object About : Screen("settings/about")
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, Icons.Default.Home, "首页"),
    BottomNavItem(Screen.Agents.route, Icons.Default.SmartToy, "智能体"),
    BottomNavItem(Screen.Chats.route, Icons.Default.Chat, "对话"),
    BottomNavItem(Screen.Groups.route, Icons.Default.Groups, "群聊"),
    BottomNavItem(Screen.Settings.route, Icons.Default.Settings, "设置")
)
