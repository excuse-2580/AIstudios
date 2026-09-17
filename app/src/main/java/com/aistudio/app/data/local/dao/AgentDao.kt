package com.aistudio.app.data.local.dao

import androidx.room.*
import com.aistudio.app.data.local.entity.AgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents ORDER BY updatedAt DESC")
    fun getAllAgents(): Flow<List<AgentEntity>>
    
    @Query("SELECT * FROM agents WHERE id = :id")
    suspend fun getAgentById(id: String): AgentEntity?
    
    @Query("SELECT * FROM agents WHERE id = :id")
    fun getAgentByIdFlow(id: String): Flow<AgentEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: AgentEntity)
    
    @Update
    suspend fun updateAgent(agent: AgentEntity)
    
    @Delete
    suspend fun deleteAgent(agent: AgentEntity)
    
    @Query("DELETE FROM agents WHERE id = :id")
    suspend fun deleteAgentById(id: String)
    
    @Query("SELECT COUNT(*) FROM agents")
    suspend fun getAgentCount(): Int
}
