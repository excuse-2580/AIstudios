package com.aistudio.app.ui.screens.chat

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aistudio.app.service.CallManager
import com.aistudio.app.service.CallState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCallScreen(
    chatId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val callManager = remember { CallManager() }
    val callState by callManager.callState.collectAsState()
    
    var hasPermissions by remember { mutableStateOf(false) }
    var callDuration by remember { mutableStateOf(0) }
    
    // 权限请求
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
        if (hasPermissions) {
            callManager.startCall(chatId, "AI智能体")
        }
    }
    
    LaunchedEffect(Unit) {
        val requiredPermissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        permissionLauncher.launch(requiredPermissions.toTypedArray())
    }
    
    // 通话计时
    LaunchedEffect(callState.state) {
        if (callState.state == CallState.CONNECTED) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                callDuration++
                callManager.updateDuration(callDuration)
            }
        }
    }
    
    LaunchedEffect(callState.state) {
        if (callState.state == CallState.DISCONNECTED) {
            kotlinx.coroutines.delay(500)
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("语音通话", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { callManager.endCall() }) {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Caller Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
            ) {
                // 状态指示
                when (callState.state) {
                    CallState.CONNECTING -> {
                        CircularProgressIndicator(modifier = Modifier.size(120.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "正在连接...",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    CallState.CONNECTED -> {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = callState.peerName.ifEmpty { "AI智能体" },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatDuration(callDuration),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    CallState.DISCONNECTED -> {
                        Icon(
                            Icons.Default.CallEnd,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "通话已结束",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    else -> {}
                }
            }
            
            // Call Controls
            if (callState.state == CallState.CONNECTED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Mute Button
                    FloatingActionButton(
                        onClick = { callManager.toggleMute() },
                        containerColor = if (callState.isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(
                            if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (callState.isMuted) "取消静音" else "静音"
                        )
                    }
                    
                    // End Call Button
                    LargeFloatingActionButton(
                        onClick = { callManager.endCall() },
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Icon(
                            Icons.Default.CallEnd,
                            contentDescription = "结束通话",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    
                    // Speaker Button
                    FloatingActionButton(
                        onClick = { callManager.toggleSpeaker() },
                        containerColor = if (callState.isSpeakerOn) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            if (callState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (callState.isSpeakerOn) "关闭扬声器" else "开启扬声器"
                        )
                    }
                }
            } else if (callState.state == CallState.CONNECTING) {
                OutlinedButton(
                    onClick = { callManager.endCall() }
                ) {
                    Text("取消")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
