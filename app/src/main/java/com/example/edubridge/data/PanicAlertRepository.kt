package com.example.edubridge.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PanicAlert(
    val studentName: String,
    val location: LatLng,
    val timestamp: Long = System.currentTimeMillis()
)
object PanicAlertRepository {
    private val _activeAlert = MutableStateFlow<PanicAlert?>(null)
    val activeAlert = _activeAlert.asStateFlow()

    fun triggerAlert(studentName: String, location: LatLng) {
        _activeAlert.value = PanicAlert(studentName = studentName, location = location)
        // Aquí, en una app real, enviarías la notificación Push al backend.
    }

    fun clearAlert() {
        _activeAlert.value = null
    }
}
    