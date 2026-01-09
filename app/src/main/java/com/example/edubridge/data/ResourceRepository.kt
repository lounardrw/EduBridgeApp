package com.example.edubridge.data

import com.example.edubridge.data.local.dao.ResourceDao
import com.example.edubridge.data.local.entitymodel.ResourceEntity
import com.example.edubridge.data.local.entitymodel.Contenido
import kotlinx.coroutines.flow.Flow

class ResourceRepository(
    private val resourceDao: ResourceDao
) {
    // 1. Obtener todos los recursos
    fun getAllResources(): Flow<List<ResourceEntity>> = resourceDao.getAllResources()

    // 2. Insertar nuevo (CREATE)
    suspend fun insertResource(title: String, author: String, url: String) {
        val newResource = ResourceEntity(
            contenido = Contenido(title = title, description = "Recurso de biblioteca", url = url),
            author = author
        )
        resourceDao.insert(newResource)
    }

    // 3. Actualizar existente (UPDATE) - SOLUCIONA EL ERROR 1
    suspend fun updateResource(resource: ResourceEntity) {
        resourceDao.update(resource)
    }

    // 4. Eliminar por ID (DELETE) - SOLUCIONA EL ERROR 2
    suspend fun deleteResourceById(id: Int) {
        resourceDao.deleteById(id)
    }
}