package com.aistudio.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aistudio.app.data.local.dao.*
import com.aistudio.app.data.local.entity.*

@Database(
    entities = [
        AgentEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        ChatParticipantEntity::class,
        GroupEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AIDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun chatParticipantDao(): ChatParticipantDao
    abstract fun groupDao(): GroupDao
}
