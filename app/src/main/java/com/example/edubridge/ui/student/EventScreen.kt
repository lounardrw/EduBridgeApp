package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//prueba
@Composable
fun EventsScreen() {
    // TODO: Aquí se mostrará el calendario y los avisos.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Eventos y Avisos")
    }
}
    