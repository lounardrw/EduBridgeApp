package com.example.edubridge.data

import com.example.edubridge.data.local.dao.ResourceDao
import com.example.edubridge.data.local.entitymodel.ResourceEntity
import com.example.edubridge.data.local.entitymodel.Contenido
import kotlinx.coroutines.flow.Flow

class ResourceRepository(
    private val resourceDao: ResourceDao
) {
    // Simplemente expone los datos desde Room
    fun getAllResources(): Flow<List<ResourceEntity>> {
        return resourceDao.getAllResources()
    }

    // Método para simular la adición de recursos (se usará en ManageLibraryScreen)
    suspend fun insertResource(title: String, author: String, url: String) {
        val newResource = ResourceEntity(
            contenido = Contenido(title = title, description = "Recurso de biblioteca", url = url),
            author = author
        )
        resourceDao.insert(newResource)
    }

}
