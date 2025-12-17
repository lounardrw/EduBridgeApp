package com.example.edubridge.filestream

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

/**
 * Gestor de Archivos para almacenamiento interno.
 * Centraliza la lógica de guardar y leer recursos locales (PDFs).
 */
class FileManager(private val context: Context) {

    private val TAG = "FileManager"

    //Devuelve el directorio donde se almacenan los recursos descargados.
    private fun getResourcesDirectory(): File {
        val dir = File(context.filesDir, "resources")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    fun saveFile(inputStream: InputStream, filename: String): File? {
        val file = File(getResourcesDirectory(), filename)
        try {
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            Log.d(TAG, "Archivo guardado exitosamente en: ${file.absolutePath}")
            return file
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar el archivo '$filename': ${e.message}")
            return null
        }
    }
    fun fileExists(filename: String): Boolean {
        val file = File(getResourcesDirectory(), filename)
        return file.exists() && file.isFile && file.length() > 0
    }
    fun getFile(filename: String): File? {
        val file = File(getResourcesDirectory(), filename)
        return if (fileExists(filename)) file else null
    }
    fun generateFilename(resourceTitle: String, resourceId: String): String {
        // Eliminar caracteres no deseados y usar un hash o ID para seguridad.
        val safeTitle = resourceTitle
            .replace(Regex("[^a-zA-Z0-9.-]"), "_") // Reemplaza no alfanuméricos por guion bajo
            .lowercase()
        return "${resourceId}_${safeTitle}.pdf"
    }
}