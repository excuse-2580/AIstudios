package com.aistudio.app.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 屏幕共享状态
 */
enum class ScreenShareState {
    IDLE,
    REQUESTING,
    SHARING,
    PAUSED,
    STOPPED
}

/**
 * 屏幕共享信息
 */
data class ScreenShareInfo(
    val chatId: String = "",
    val state: ScreenShareState = ScreenShareState.IDLE,
    val duration: Int = 0,  // 秒
    val isFullScreen: Boolean = false
)

/**
 * 屏幕共享服务
 * 处理屏幕录制和共享逻辑
 */
@AndroidEntryPoint
class ScreenShareService : Service() {
    
    private val binder = ScreenShareBinder()
    
    private val _shareState = MutableStateFlow(ScreenShareInfo())
    val shareState: StateFlow<ScreenShareInfo> = _shareState.asStateFlow()
    
    inner class ScreenShareBinder : Binder() {
        fun getService(): ScreenShareService = this@ScreenShareService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            VoiceCallService.ACTION_START_CALL -> {
                val chatId = intent.getStringExtra(VoiceCallService.EXTRA_CHAT_ID) ?: return START_NOT_STICKY
                startForeground(VoiceCallService.NOTIFICATION_ID, createNotification("屏幕共享中..."))
            }
            VoiceCallService.ACTION_END_CALL -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }
    
    /**
     * 开始屏幕共享请求
     */
    fun requestScreenShare(activity: Activity, chatId: String) {
        val mediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        // 注意：这会启动系统截图选择器，用户需要授权
        activity.startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            REQUEST_CODE_SCREEN_CAPTURE
        )
        
        _shareState.value = ScreenShareInfo(
            chatId = chatId,
            state = ScreenShareState.REQUESTING
        )
    }
    
    /**
     * 开始共享（获得授权后调用）
     */
    fun startSharing(resultCode: Int, data: Intent) {
        // TODO: 使用 WebRTC 或其他库初始化屏幕流
        _shareState.value = _shareState.value.copy(state = ScreenShareState.SHARING)
    }
    
    /**
     * 暂停共享
     */
    fun pauseSharing() {
        _shareState.value = _shareState.value.copy(state = ScreenShareState.PAUSED)
    }
    
    /**
     * 继续共享
     */
    fun resumeSharing() {
        _shareState.value = _shareState.value.copy(state = ScreenShareState.SHARING)
    }
    
    /**
     * 停止共享
     */
    fun stopSharing() {
        _shareState.value = _shareState.value.copy(state = ScreenShareState.STOPPED)
    }
    
    /**
     * 切换全屏模式
     */
    fun toggleFullScreen() {
        _shareState.value = _shareState.value.copy(
            isFullScreen = !_shareState.value.isFullScreen
        )
    }
    
    private fun createNotification(content: String): android.app.Notification {
        val pendingIntent = android.app.PendingIntent.getActivity(
            this,
            0,
            Intent(this, com.aistudio.app.MainActivity::class.java),
            android.app.PendingIntent.FLAG_IMMUTABLE
        )
        
        return androidx.core.app.NotificationCompat.Builder(this, VoiceCallService.CHANNEL_ID)
            .setContentTitle("AI智能体工坊")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    companion object {
        const val REQUEST_CODE_SCREEN_CAPTURE = 1002
    }
}
