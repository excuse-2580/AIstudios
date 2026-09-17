package com.aistudio.app.domain.model

import java.util.UUID

data class CallSession(
    val id: String = UUID.randomUUID().toString(),
    val type: CallType,
    val chatId: String,
    val participants: List<String> = emptyList(),
    val status: CallStatus = CallStatus.INITIATED,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val duration: Long = 0,
    val screenShareSessionId: String? = null
)

enum class CallType {
    VOICE,
    VIDEO,
    SCREEN_SHARE
}

enum class CallStatus {
    INITIATED,
    RINGING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ENDED,
    FAILED,
    DECLINED
}

data class ScreenShareSession(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val hostId: String,
    val viewerIds: List<String> = emptyList(),
    val status: ScreenShareStatus = ScreenShareStatus.SHARED,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null
)

enum class ScreenShareStatus {
    SHARED,
    PAUSED,
    ENDED
}
