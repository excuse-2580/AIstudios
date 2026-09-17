package com.aistudio.app.ui.screens.chat

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aistudio.app.service.ScreenShareInfo
import com.aistudio.app.service.ScreenShareService
import com.aistudio.app.service.ScreenShareState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenShareScreen(
    chatId: String,
    navController: NavController
) {
    val context = LocalContext.current
    var isSharing by remember { mutableStateOf(true) }
    var shareDuration by remember { mutableStateOf(0) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    // 屏幕共享结果处理
    val screenCaptureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            // 授权成功，开始共享
            // TODO: 传递给 ScreenShareService
            isSharing = true
        } else {
            // 用户拒绝
            showPermissionDialog = true
        }
    }
    
    LaunchedEffect(Unit) {
        // 请求屏幕共享权限
        val mediaProjectionManager = context.getSystemService(android.content.Context.MEDIA_PROJECTION_SERVICE) as android.media.projection.MediaProjectionManager
        screenCaptureLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }
    
    // 共享计时
    LaunchedEffect(isSharing) {
        if (isSharing) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                shareDuration++
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("屏幕共享", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Preview Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isSharing) {
                        Icon(
                            Icons.Default.ScreenShare,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "正在共享屏幕",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "已共享 ${formatDuration(shareDuration)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Icon(
                            Icons.Default.StopScreenShare,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "屏幕共享已暂停",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Pause/Resume Button
                FloatingActionButton(
                    onClick = { isSharing = !isSharing },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        if (isSharing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isSharing) "暂停共享" else "继续共享"
                    )
                }
                
                // End Share Button
                LargeFloatingActionButton(
                    onClick = { navController.popBackStack() },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "停止共享",
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                // Fullscreen Button
                FloatingActionButton(
                    onClick = { /* TODO: 全屏模式 */ },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "全屏")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "当前正在共享您的屏幕，所有参与者都将看到您的屏幕内容。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
    
    // 权限拒绝提示
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("权限被拒绝") },
            text = { Text("屏幕共享需要您授权屏幕截图权限。请在设置中开启后重试。") },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("确定")
                }
            }
        )
    }
}

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
