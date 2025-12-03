package com.example.edubridge.ui.teacher

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edubridge.data.PanicAlertRepository
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

// Anotación para solucionar el error de TopAppBar
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertMapScreen(onDismiss: () -> Unit) {
    val alert by PanicAlertRepository.activeAlert.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Alerta de Seguridad Activa") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (alert != null) {
                val currentAlert = alert!!

                // Actualizar la posición de la cámara cuando la alerta cambia
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentAlert.location, 15f)

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    // Marcador en la ubicación del alumno (CORRECCIÓN FINAL)
                    Marker(
                        // Se crea un MarkerState y se le pasa la posición
                        state = MarkerState(position = currentAlert.location),
                        title = "¡Alerta de ${currentAlert.studentName}!",
                        snippet = "Ubicación de la emergencia"
                    )
                }

                // Botón para descartar la alerta
                Button(
                    onClick = {
                        PanicAlertRepository.clearAlert()
                        onDismiss() // Regresa a la pantalla anterior
                    },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                ) {
                    Text("Marcar como Atendido y Cerrar")
                }

            } else {
                // No hay alerta activa
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay alertas activas en este momento.")
                }
            }
        }
    }
}
