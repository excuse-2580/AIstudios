package com.aistudio.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String,
    val personality: String,
    val expertise: String, // JSON array as string
    val voiceType: String,
    val systemPrompt: String,
    val temperature: Float,
    val maxTokens: Int,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val name: String,
    val avatarUrl: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isPinned: Boolean,
    val isMuted: Boolean
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderType: String,
    val content: String,
    val timestamp: Long,
    val status: String,
    val replyTo: String?
)

@Entity(tableName = "chat_participants")
data class ChatParticipantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: String,
    val participantId: String,
    val participantName: String,
    val participantAvatar: String,
    val role: String,
    val isOnline: Boolean
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String,
    val memberIds: String, // JSON array
    val agentIds: String, // JSON array
    val createdBy: String,
    val createdAt: Long,
    val maxMembers: Int,
    val isPublic: Boolean
)
