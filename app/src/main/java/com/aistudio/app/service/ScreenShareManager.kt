package com.aistudio.app.service

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
