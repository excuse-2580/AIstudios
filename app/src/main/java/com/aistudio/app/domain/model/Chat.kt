package com.aistudio.app.domain.model

import java.util.UUID

data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val type: ChatType,
    val name: String = "",
    val avatarUrl: String = "",
    val participants: List<Participant> = emptyList(),
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isMuted: Boolean = false
)

enum class ChatType {
    PRIVATE,
    GROUP,
    AGENT_CHAT
}

data class Participant(
    val id: String,
    val name: String,
    val avatarUrl: String = "",
    val role: ParticipantRole = ParticipantRole.MEMBER,
    val isOnline: Boolean = false,
    val lastSeen: Long? = null
)

enum class ParticipantRole {
    OWNER,
    ADMIN,
    MEMBER
}

data class Group(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val avatarUrl: String = "",
    val memberIds: List<String> = emptyList(),
    val agentIds: List<String> = emptyList(),
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val maxMembers: Int = 50,
    val isPublic: Boolean = true
)
