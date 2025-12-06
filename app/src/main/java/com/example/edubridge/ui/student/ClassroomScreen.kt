package com.example.edubridge.ui.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // Controla la navegación entre destinos.
import com.example.edubridge.Destinations // Rutas centralizadas.

// Los grados de Secundaria (los únicos que se usan en la app).
val schoolGrades = listOf(
    "1° Secundaria",
    "2° Secundaria",
    "3° Secundaria"
)

// COMPOSABLE PRINCIPAL

/**
 * Pantalla de Aulas Interactivas.
 * Muestra los grados escolares disponibles y permite la navegación al módulo de Quizz.
 * @param navController El controlador de navegación para cambiar de pantalla.
 */
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
