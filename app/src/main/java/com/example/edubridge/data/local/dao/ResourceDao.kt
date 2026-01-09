package com.example.edubridge.data.local.dao

import androidx.room.*
import com.example.edubridge.data.local.entitymodel.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao : BaseDao<ResourceEntity> {
    @Query("SELECT * FROM recursos ORDER BY titulo ASC")
    fun getAllResources(): Flow<List<ResourceEntity>>

    @Query("DELETE FROM recursos WHERE id = :resourceId")
    suspend fun deleteById(resourceId: Int)
}