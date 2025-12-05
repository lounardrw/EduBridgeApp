package com.example.edubridge.ui.teacher

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edubridge.data.PanicAlertRepository
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.* import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.BitmapDescriptorFactory // Necesario para el marcador

/**
 * Mapa de Alertas de Pánico (Luis).
 * Muestra la ubicación del alumno en peligro en un mapa en tiempo real.
 * @param onDismiss Función para cerrar la pantalla y volver al dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun AlertMapScreen(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    // Escucha la alerta activa en tiempo real.
    val alert by PanicAlertRepository.activeAlert.collectAsState(initial = null)

    // Estado de la cámara para poder mover el mapa al punto de la alerta.
    val cameraPositionState = rememberCameraPositionState {
        // Posición por defecto si no hay alerta
        position = CameraPosition.fromLatLngZoom(com.google.android.gms.maps.model.LatLng(19.0, -98.0), 10f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alert != null) "Alerta Activa: Localizando..." else "Alerta Resuelta") },
                navigationIcon = {
                    // Botón para volver al panel del profesor.
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver al Panel")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {

            if (alert != null) {
                val currentAlert = alert!!

                // Mueve la cámara al punto de la alerta cuando los datos se actualizan.
                LaunchedEffect(currentAlert.location) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentAlert.location, 15f)
                }

                // 1. Componente Google Map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                ) {
                    // 2. Marcador en la ubicación del alumno
                    Marker(
                        state = MarkerState(position = currentAlert.location),
                        title = "¡Alerta de Pánico!",
                        snippet = "Alumno: ${currentAlert.studentName}",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }

                // 3. Botón para descartar la alerta (Flotante)
                Button(
                    onClick = {
                        PanicAlertRepository.clearAlert() // Lógica: Borra la alerta.
                        onDismiss() // Regresa a la pantalla anterior.
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
                ) {
                    Text("Marcar como Atendido y Cerrar", style = MaterialTheme.typography.titleMedium)
                }

            } else {
                // Estado cuando no hay alerta activa (se cerró o nunca se activó)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay alertas de seguridad activas en este momento.",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}