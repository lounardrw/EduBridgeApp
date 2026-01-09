package com.example.edubridge.filestream

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

//Se encarga de descargar archivos de una URL externa y guardarlos localmente.
class DownloadManager(context: Context) {

    private val fileManager = FileManager(context)
    private val httpClient = OkHttpClient()
    private val TAG = "DownloadManager"

//Descarga un archivo de una URL y lo guarda localmente.
    suspend fun downloadFile(url: String, filename: String): File? {
        //Verificar si el archivo ya existe
        if (fileManager.fileExists(filename)) {
            Log.i(TAG, "El archivo '$filename' ya existe localmente. Omitiendo descarga.")
            return fileManager.getFile(filename)
        }

        // Si no existe, proceder con la descarga
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Fallo de red: Código ${response.code}")

                    response.body?.byteStream()?.let { inputStream ->
                        // Guardar el stream usando FileManager
                        return@withContext fileManager.saveFile(inputStream, filename)
                    }
                    null
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error en la descarga de '$url': ${e.message}")
                null
            }
        }
    }
}