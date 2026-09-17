package com.aistudio.app.domain.model

data class Agent(
    val id: String,
    val name: String,
    val description: String = "",
    val personality: String = "",
    val expertise: String = "",
    val voiceType: VoiceType = VoiceType.NEUTRAL,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val avatarUrl: String = "",
    val modelName: String? = null,  // AI 模型名称，如 "gpt-4", "claude-3-sonnet"
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 语音类型
 */
enum class VoiceType {
    FEMALE_WARM,    // 温柔女声
    FEMALE_BRIGHT,  // 明亮女声
    MALE_WARM,      // 温暖男声
    MALE_DEEP,      // 低沉男声
    NEUTRAL,        // 中性
    ROBOTIC         // 机器人
}
