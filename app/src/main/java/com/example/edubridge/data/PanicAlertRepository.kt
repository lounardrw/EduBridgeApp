package com.example.edubridge.data

import android.util.Log
import com.example.edubridge.network.AlertRequest
import com.example.edubridge.network.ApiService
import com.example.edubridge.data.local.dao.AlertaDao
import com.example.edubridge.data.local.entitymodel.AlertaUbicacion
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

// DTO para la UI de la Alerta (MapScreen)
data class PanicAlert(
    val studentName: String,
    val location: LatLng,
    val userId: String,
    val timestamp: Long = System.currentTimeMillis()
)

// Repositorio central de Pánico
object PanicAlertRepository {
    private val _activeAlert = MutableStateFlow<PanicAlert?>(null)
    val activeAlert = _activeAlert.asStateFlow()
    private val apiScope = CoroutineScope(Dispatchers.IO)

    private lateinit var alertaDao: AlertaDao
    private const val MOCK_USER_ID_INT = 1

    fun initialize(dao: AlertaDao) {
        alertaDao = dao
        // Cargar el estado al iniciar la aplicación para mantener la persistencia
        apiScope.launch {
            loadActiveAlertFromBackend()
        }
    }

    //LÓGICA DE RESTAURACIÓN DE ESTADO
    private suspend fun loadActiveAlertFromBackend() {
        try {
            val response = ApiService.api.checkActiveAlert()

            if (response.isSuccessful && response.body()?.active == true) {
                // El mapeo es defensivo: si no vienen las coordenadas o el ID, falla en el if
                val data = response.body()!!.data!!
                val userIdStr = data.user_id.toString()

                if (data.latitude != null && data.longitude != null) {
                    _activeAlert.value = PanicAlert(
                        studentName = "Alumno $userIdStr",
                        location = LatLng(data.latitude, data.longitude),
                        userId = userIdStr,
                        timestamp = System.currentTimeMillis()
                    )
                    Log.d("PanicAlert", "Alerta activa restaurada desde el servidor.")
                }
            }
        } catch (e: Exception) {
            Log.e("PanicAlert", "Error al verificar alerta activa (persistencia): ${e.message}")
        }
    }

    //LÓGICA DE ACTIVACIÓN (ALUMNO)
    fun triggerAlert(studentName: String, location: LatLng, userId: String) {

        // Establece el estado activo INMEDIATAMENTE
        _activeAlert.value = PanicAlert(studentName = studentName, location = location, userId = userId)

        val request = AlertRequest(
            user_id = MOCK_USER_ID_INT,
            latitude = location.latitude,
            longitude = location.longitude
        )

        apiScope.launch {
            try {
                val response = ApiService.api.createPanicAlert(request)

                if (!response.isSuccessful || response.body()?.ok != true) {
                    val errorReason = response.body()?.error ?: "Server Error: ${response.code()}"
                    saveLocally(studentName, location, errorReason)
                }
            } catch (e: IOException) {
                saveLocally(studentName, location, "Error de Red: ${e.message}")
            } catch (e: Exception) {
                saveLocally(studentName, location, "Excepción: ${e.message}")
            }
        }
    }

    private suspend fun saveLocally(studentName: String, location: LatLng, reason: String) {
        val localAlert = AlertaUbicacion(
            userId = studentName,
            latitude = location.latitude,
            longitude = location.longitude
        )
        alertaDao.insert(localAlert)
    }

    //Llamada solo desde AlertMapScreen (Profesor) para descartar la alerta.
    fun clearAlert() {
        //Borra de la memoria local
        _activeAlert.value = null
        Log.d("PanicAlert", "Alerta descartada por el profesor (Memoria).")

        //Llama al servidor PHP para marcarla como ATENDIDA
        apiScope.launch {
            try {
                //Endpoint para marcar la alerta como is_active=0 en MySQL
                val response = ApiService.api.clearPanicAlert(mapOf("status" to 0))

                if (response.isSuccessful && response.body()?.ok == true) {
                    Log.d("PanicAlert", "Alerta marcada como atendida en MySQL.")
                } else {
                    Log.e("PanicAlert", "Fallo al marcar alerta como atendida en MySQL.")
                }
            } catch (e: Exception) {
                Log.e("PanicAlert", "Error de red al intentar desactivar alerta en MySQL: ${e.message}")
            }
        }
    }
}