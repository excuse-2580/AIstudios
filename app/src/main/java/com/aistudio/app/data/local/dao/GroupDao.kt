package com.aistudio.app.data.local.dao

import androidx.room.*
import com.aistudio.app.data.local.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>
    
    @Query("SELECT * FROM groups WHERE id = :id")
    suspend fun getGroupById(id: String): GroupEntity?
    
    @Query("SELECT * FROM groups WHERE id = :id")
    fun getGroupByIdFlow(id: String): Flow<GroupEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)
    
    @Update
    suspend fun updateGroup(group: GroupEntity)
    
    @Delete
    suspend fun deleteGroup(group: GroupEntity)
    
    @Query("DELETE FROM groups WHERE id = :id")
    suspend fun deleteGroupById(id: String)
    
    @Query("SELECT * FROM groups WHERE createdBy = :userId ORDER BY createdAt DESC")
    fun getGroupsCreatedBy(userId: String): Flow<List<GroupEntity>>
}
