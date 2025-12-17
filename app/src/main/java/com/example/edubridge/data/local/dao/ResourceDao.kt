package com.example.edubridge.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.edubridge.data.local.entitymodel.ResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la entidad Resource (Recursos de Biblioteca).
 * Hereda del BaseDao.
 */
@Dao
interface ResourceDao : BaseDao<ResourceEntity> {
    @Query("SELECT * FROM recursos ORDER BY titulo ASC")
    fun getAllResources(): Flow<List<ResourceEntity>>
}