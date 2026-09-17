package com.aistudio.app.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 通话状态
 */
enum class CallState {
    IDLE,
    CONNECTING,
    RINGING,
    CONNECTED,
    ON_HOLD,
    DISCONNECTED
}

/**
 * 通话信息
 */
data class CallInfo(
    val callId: String = "",
    val chatId: String = "",
    val peerName: String = "",
    val state: CallState = CallState.IDLE,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isScreenSharing: Boolean = false,
    val duration: Int = 0  // 秒
)

/**
 * 通话管理器
 * 管理语音通话和屏幕共享的逻辑
 */
@Singleton
class CallManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _callState = MutableStateFlow(CallInfo())
    val callState: StateFlow<CallInfo> = _callState.asStateFlow()
    
    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    /**
     * 发起通话
     */
    fun startCall(chatId: String, peerName: String) {
        _callState.value = CallInfo(
            callId = System.currentTimeMillis().toString(),
            chatId = chatId,
            peerName = peerName,
            state = CallState.CONNECTING
        )
        
        // 切换到通话模式
        setSpeakerphoneOn(true)
    }
    
    /**
     * 通话已连接
     */
    fun onCallConnected() {
        _callState.value = _callState.value.copy(state = CallState.CONNECTED)
    }
    
    /**
     * 结束通话
     */
    fun endCall() {
        _callState.value = _callState.value.copy(state = CallState.DISCONNECTED)
        
        // 重置音频模式
        audioManager.mode = AudioManager.MODE_NORMAL
        
        // 延迟重置状态
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _callState.value = CallInfo()
        }, 1000)
    }
    
    /**
     * 静音/取消静音
     */
    fun toggleMute() {
        val newMuted = !_callState.value.isMuted
        _callState.value = _callState.value.copy(isMuted = newMuted)
        
        // TODO: 实际控制麦克风
        audioManager.isMicrophoneMute = newMuted
    }
    
    /**
     * 切换扬声器
     */
    fun toggleSpeaker() {
        val newSpeakerOn = !_callState.value.isSpeakerOn
        _callState.value = _callState.value.copy(isSpeakerOn = newSpeakerOn)
        setSpeakerphoneOn(newSpeakerOn)
    }
    
    private fun setSpeakerphoneOn(on: Boolean) {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = on
    }
    
    /**
     * 更新通话时长
     */
    fun updateDuration(seconds: Int) {
        _callState.value = _callState.value.copy(duration = seconds)
    }
    
    /**
     * 是否有正在进行的通话
     */
    fun hasActiveCall(): Boolean {
        return _callState.value.state == CallState.CONNECTED ||
               _callState.value.state == CallState.CONNECTING ||
               _callState.value.state == CallState.RINGING
    }
    
    /**
     * 权限检查
     */
    fun hasRequiredPermissions(activity: Activity): Boolean {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        return permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * 请求权限
     */
    fun requestPermissions(activity: Activity) {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            REQUEST_CODE_CALL_PERMISSIONS
        )
    }
    
    companion object {
        const val REQUEST_CODE_CALL_PERMISSIONS = 1001
    }
}
