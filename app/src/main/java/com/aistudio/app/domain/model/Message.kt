package com.aistudio.app.domain.model

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val senderId: String,
    val senderType: SenderType,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val replyTo: String? = null,
    val attachments: List<Attachment> = emptyList()
)

enum class SenderType {
    USER,
    AGENT,
    SYSTEM
}

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

data class Attachment(
    val id: String = UUID.randomUUID().toString(),
    val type: AttachmentType,
    val url: String,
    val thumbnailUrl: String? = null,
    val mimeType: String,
    val fileName: String,
    val fileSize: Long = 0
)

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    SCREEN_SHARE
}
