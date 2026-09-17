package com.aistudio.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import com.aistudio.app.service.CallInfo
import com.aistudio.app.service.CallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 语音通话 ViewModel
 * 通过 Hilt 注入 CallManager（单例），向 UI 暴露通话状态与操作
 */
@HiltViewModel
class CallViewModel @Inject constructor(
    private val callManager: CallManager
) : ViewModel() {

    val callState: StateFlow<CallInfo> = callManager.callState

    fun startCall(chatId: String, peerName: String) {
        callManager.startCall(chatId, peerName)
    }

    fun endCall() = callManager.endCall()

    fun toggleMute() = callManager.toggleMute()

    fun toggleSpeaker() = callManager.toggleSpeaker()

    fun updateDuration(duration: Int) = callManager.updateDuration(duration)
}
