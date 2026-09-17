package com.aistudio.app.data.repository

import com.aistudio.app.data.local.dao.GroupDao
import com.aistudio.app.data.local.entity.GroupEntity
import com.aistudio.app.domain.model.Group
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val groupDao: GroupDao,
    private val gson: Gson
) {
    fun getAllGroups(): Flow<List<Group>> {
        return groupDao.getAllGroups().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getGroupById(id: String): Flow<Group?> {
        return groupDao.getGroupByIdFlow(id).map { it?.toDomain() }
    }
    
    suspend fun getGroupByIdOnce(id: String): Group? {
        return groupDao.getGroupById(id)?.toDomain()
    }
    
    suspend fun createGroup(group: Group) {
        groupDao.insertGroup(group.toEntity())
    }
    
    suspend fun updateGroup(group: Group) {
        groupDao.updateGroup(group.toEntity())
    }
    
    suspend fun deleteGroup(groupId: String) {
        groupDao.deleteGroupById(groupId)
    }
    
    private fun GroupEntity.toDomain(): Group {
        val memberList: List<String> = try {
            gson.fromJson(memberIds, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        
        val agentList: List<String> = try {
            gson.fromJson(agentIds, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        
        return Group(
            id = id,
            name = name,
            description = description,
            avatarUrl = avatarUrl,
            memberIds = memberList,
            agentIds = agentList,
            createdBy = createdBy,
            createdAt = createdAt,
            isPublic = isPublic,
            maxMembers = maxMembers
        )
    }
    
    private fun Group.toEntity(): GroupEntity {
        return GroupEntity(
            id = id,
            name = name,
            description = description,
            avatarUrl = avatarUrl,
            memberIds = gson.toJson(memberIds),
            agentIds = gson.toJson(agentIds),
            createdBy = createdBy,
            createdAt = createdAt,
            maxMembers = maxMembers,
            isPublic = isPublic
        )
    }
}
