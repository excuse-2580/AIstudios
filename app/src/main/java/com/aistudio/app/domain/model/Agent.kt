package com.aistudio.app.domain.model

import java.util.UUID

data class Agent(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val avatarUrl: String = "",
    val personality: String = "",
    val expertise: List<String> = emptyList(),
    val voiceType: VoiceType = VoiceType.NEUTRAL,
    val systemPrompt: String = "",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val modelName: String? = null,  // AI 模型名称，如 "gpt-4", "claude-3-sonnet"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = 0L
)

/**
 * 语音类型
 */
enum class VoiceType(val displayName: String) {
    FEMALE_WARM("温柔女声"),    // 温柔女声
    FEMALE_BRIGHT("明亮女声"),  // 明亮女声
    MALE_WARM("温暖男声"),      // 温暖男声
    MALE_DEEP("低沉男声"),      // 低沉男声
    NEUTRAL("中性声"),          // 中性
    ROBOTIC("机器人声")         // 机器人
}
