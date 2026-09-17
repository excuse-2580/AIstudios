package com.aistudio.app.data.repository

import com.aistudio.app.data.local.dao.AgentDao
import com.aistudio.app.data.local.entity.AgentEntity
import com.aistudio.app.domain.model.Agent
import com.aistudio.app.domain.model.VoiceType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepository @Inject constructor(
    private val agentDao: AgentDao,
    private val gson: Gson
) {
    fun getAllAgents(): Flow<List<Agent>> {
        return agentDao.getAllAgents().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getAgentById(id: String): Flow<Agent?> {
        return agentDao.getAgentByIdFlow(id).map { it?.toDomain() }
    }
    
    suspend fun getAgentByIdOnce(id: String): Agent? {
        return agentDao.getAgentById(id)?.toDomain()
    }
    
    suspend fun createAgent(agent: Agent) {
        agentDao.insertAgent(agent.toEntity())
    }
    
    suspend fun updateAgent(agent: Agent) {
        agentDao.updateAgent(agent.toEntity())
    }
    
    suspend fun deleteAgent(agentId: String) {
        agentDao.deleteAgentById(agentId)
    }
    
    suspend fun getAgentCount(): Int {
        return agentDao.getAgentCount()
    }
    
    private fun AgentEntity.toDomain(): Agent {
        val expertiseList: List<String> = try {
            gson.fromJson(expertise, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        
        return Agent(
            id = id,
            name = name,
            description = description,
            avatarUrl = avatarUrl,
            personality = personality,
            expertise = expertiseList,
            voiceType = VoiceType.entries.find { it.name == voiceType } ?: VoiceType.FEMALE_WARM,
            systemPrompt = systemPrompt,
            temperature = temperature,
            maxTokens = maxTokens,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun Agent.toEntity(): AgentEntity {
        return AgentEntity(
            id = id,
            name = name,
            description = description,
            avatarUrl = avatarUrl,
            personality = personality,
            expertise = gson.toJson(expertise),
            voiceType = voiceType.name,
            systemPrompt = systemPrompt,
            temperature = temperature,
            maxTokens = maxTokens,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
