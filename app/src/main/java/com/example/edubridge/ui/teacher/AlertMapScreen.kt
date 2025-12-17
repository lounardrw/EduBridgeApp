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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*


/**
 * Pantalla de visualización de la Alerta de Pánico.
 * Muestra la ubicación del alumno en peligro en un mapa de Google Maps.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "MissingPermission") // Permiso se maneja en StudentHomeScreen
@Composable
fun AlertMapScreen(onDismiss: () -> Unit) {
    // Escucha el estado de la alerta en tiempo real.
    val alert by PanicAlertRepository.activeAlert.collectAsState(initial = null)

    // Estado de la cámara para mover el mapa.
    val initialLocation = LatLng(19.0, -98.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 10f)
    }

    // Lógica para enfocar el mapa en la alerta si existe.
    LaunchedEffect(alert) {
        alert?.let {
            // Usamos CameraUpdateFactory (ahora resuelto) para animar el cambio de vista
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it.location, 15f),
                1000
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alert != null) "Alerta Activa: ${alert!!.studentName}" else "Alerta de Seguridad") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Componente del Mapa de Google
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Muestra el marcador SOLO si hay una alerta activa.
                alert?.let { currentAlert ->
                    Marker(
                        state = MarkerState(position = currentAlert.location),
                        title = "¡Emergencia!",
                        snippet = "Alumno: ${currentAlert.studentName}"
                    )
                }
            }

            if (alert != null) {
                // Botón para descartar la alerta
                Button(
                    onClick = {
                        PanicAlertRepository.clearAlert()
                        onDismiss() // Regresa a la pantalla anterior
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
                ) {
                    Text("Marcar como Atendido y Cerrar")
                }
            } else {
                // No hay alerta activa, muestra un mensaje
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay alertas activas en este momento. El mapa está en espera.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}