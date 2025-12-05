package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ClassroomsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), // Es una buena práctica usar el modifier en el contenedor principal
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Aulas Interactivas (Cuenca)"
        )
    }
}
