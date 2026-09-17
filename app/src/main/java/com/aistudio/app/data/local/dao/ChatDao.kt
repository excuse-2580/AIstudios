package com.aistudio.app.data.local.dao

import androidx.room.*
import com.aistudio.app.data.local.entity.ChatEntity
import com.aistudio.app.data.local.entity.MessageEntity
import com.aistudio.app.data.local.entity.ChatParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY updatedAt DESC")
    fun getAllChats(): Flow<List<ChatEntity>>
    
    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getChatById(id: String): ChatEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)
    
    @Update
    suspend fun updateChat(chat: ChatEntity)
    
    @Delete
    suspend fun deleteChat(chat: ChatEntity)
    
    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteChatById(id: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChatId(chatId: String): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(chatId: String, limit: Int): List<MessageEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Update
    suspend fun updateMessage(message: MessageEntity)
    
    @Delete
    suspend fun deleteMessage(message: MessageEntity)
    
    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesByChatId(chatId: String)
    
    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: String)
}

@Dao
interface ChatParticipantDao {
    @Query("SELECT * FROM chat_participants WHERE chatId = :chatId")
    fun getParticipantsByChatId(chatId: String): Flow<List<ChatParticipantEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ChatParticipantEntity)
    
    @Delete
    suspend fun deleteParticipant(participant: ChatParticipantEntity)
    
    @Query("DELETE FROM chat_participants WHERE chatId = :chatId AND participantId = :participantId")
    suspend fun removeParticipant(chatId: String, participantId: String)
}
