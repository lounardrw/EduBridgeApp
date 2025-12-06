package com.example.edubridge.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Mapa de Alertas de Pánico.
 * Esta pantalla muestra la ubicación del alumno en peligro en un mapa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertMapScreen(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicación de Alerta Activa") },
                navigationIcon = {
                    // Botón para volver al dashboard del profesor.
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Contenedor centrado que contendrá el mapa.
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "MAPA DE ALERTA: Aquí se integrará el componente de Google Maps y la LatLng del repositorio (Tarea de Luis).",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}