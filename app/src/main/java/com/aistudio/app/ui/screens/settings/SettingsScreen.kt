package com.aistudio.app.ui.screens.settings

@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aistudio.app.data.remote.ApiConfig
import com.aistudio.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var voiceAutoPlay by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "AI 设置",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Default.Key,
                    title = "API Key 配置",
                    subtitle = if (ApiConfig.isConfigured()) "已配置" else "未配置",
                    onClick = { navController.navigate(Screen.ApiSettings.route) }
                )
            }
            
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "外观",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "深色模式",
                    subtitle = "使用深色主题",
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }
            
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "通知",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "推送通知",
                    subtitle = "接收新消息通知",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.VolumeUp,
                    title = "提示音",
                    subtitle = "消息提示音",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
            }
            
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "语音设置",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.PlayCircle,
                    title = "语音自动播放",
                    subtitle = "自动播放语音消息",
                    checked = voiceAutoPlay,
                    onCheckedChange = { voiceAutoPlay = it }
                )
            }
            
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "关于",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Default.Info,
                    title = "版本信息",
                    subtitle = "v1.0.0"
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Default.Policy,
                    title = "隐私政策",
                    subtitle = "查看隐私政策"
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Default.Description,
                    title = "用户协议",
                    subtitle = "查看用户协议"
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Default.Code,
                    title = "开放源码",
                    subtitle = "查看开源许可"
                )
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingsClickItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
