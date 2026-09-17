package com.aistudio.app.data.repository

import com.aistudio.app.data.local.dao.ChatDao
import com.aistudio.app.data.local.dao.MessageDao
import com.aistudio.app.data.local.dao.ChatParticipantDao
import com.aistudio.app.data.local.entity.ChatEntity
import com.aistudio.app.data.local.entity.MessageEntity
import com.aistudio.app.data.local.entity.ChatParticipantEntity
import com.aistudio.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val participantDao: ChatParticipantDao
) {
    fun getAllChats(): Flow<List<Chat>> {
        return chatDao.getAllChats().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getChatById(id: String): Chat? {
        return chatDao.getChatById(id)?.toDomain()
    }
    
    suspend fun createChat(chat: Chat) {
        chatDao.insertChat(chat.toEntity())
        chat.participants.forEach { participant ->
            participantDao.insertParticipant(participant.toEntity(chat.id))
        }
    }
    
    suspend fun updateChat(chat: Chat) {
        chatDao.updateChat(chat.toEntity())
    }
    
    suspend fun deleteChat(chatId: String) {
        messageDao.deleteMessagesByChatId(chatId)
        chatDao.deleteChatById(chatId)
    }
    
    fun getMessages(chatId: String): Flow<List<Message>> {
        return messageDao.getMessagesByChatId(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun sendMessage(message: Message) {
        messageDao.insertMessage(message.toEntity())
    }
    
    suspend fun updateMessageStatus(messageId: String, status: MessageStatus) {
        messageDao.updateMessageStatus(messageId, status.name)
    }

    suspend fun updateMessage(message: Message) {
        messageDao.updateMessage(message.toEntity())
    }

    suspend fun deleteMessage(messageId: String) {
        messageDao.deleteMessageById(messageId)
    }

    suspend fun markMessagesAsRead(chatId: String) {
        messageDao.markChatMessagesRead(chatId)
    }
    
    fun getParticipants(chatId: String): Flow<List<Participant>> {
        return participantDao.getParticipantsByChatId(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    private fun ChatEntity.toDomain(): Chat {
        return Chat(
            id = id,
            type = ChatType.entries.find { it.name == type } ?: ChatType.PRIVATE,
            name = name,
            avatarUrl = avatarUrl,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isPinned = isPinned,
            isMuted = isMuted
        )
    }
    
    private fun Chat.toEntity(): ChatEntity {
        return ChatEntity(
            id = id,
            type = type.name,
            name = name,
            avatarUrl = avatarUrl,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isPinned = isPinned,
            isMuted = isMuted
        )
    }
    
    private fun MessageEntity.toDomain(): Message {
        return Message(
            id = id,
            chatId = chatId,
            senderId = senderId,
            senderType = SenderType.entries.find { it.name == senderType } ?: SenderType.USER,
            content = content,
            timestamp = timestamp,
            status = MessageStatus.entries.find { it.name == status } ?: MessageStatus.SENT,
            replyTo = replyTo
        )
    }
    
    private fun Message.toEntity(): MessageEntity {
        return MessageEntity(
            id = id,
            chatId = chatId,
            senderId = senderId,
            senderType = senderType.name,
            content = content,
            timestamp = timestamp,
            status = status.name,
            replyTo = replyTo
        )
    }
    
    private fun Participant.toEntity(chatId: String): ChatParticipantEntity {
        return ChatParticipantEntity(
            chatId = chatId,
            participantId = id,
            participantName = name,
            participantAvatar = avatarUrl,
            role = role.name,
            isOnline = isOnline
        )
    }
    
    private fun ChatParticipantEntity.toDomain(): Participant {
        return Participant(
            id = participantId,
            name = participantName,
            avatarUrl = participantAvatar,
            role = ParticipantRole.entries.find { it.name == role } ?: ParticipantRole.MEMBER,
            isOnline = isOnline
        )
    }
}
