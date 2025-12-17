package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudentEventsRoute(
    viewModel: EventViewModel = viewModel()
) {
    // Escuchamos el estado unificado
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.loading && uiState.events.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            // Pasamos el control al EventsScreen para mostrar el error sobre la lista
            EventsScreen(viewModel = viewModel)
        }
        else -> {
            // Pasamos el ViewModel directamente al Composable de la pantalla
            EventsScreen(viewModel = viewModel)
        }
    }
}