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
fun ClassroomsScreen(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selecciona tu Grado Escolar",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Muestra los grados en una lista vertical de tarjetas grandes.
        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // Una sola columna para un diseño más limpio y legible.
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(schoolGrades) { grade ->
                GradeCard(
                    grade = grade,
                    onClick = {
                        // ACCIÓN CLAVE: Navegación al QuizSelectionScreen.
                        // Se usa la URL definida en MainActivity (quiz_selection/{grade})
                        // y se reemplaza {grade} por el valor real (ej: "1° Secundaria").
                        navController.navigate("quiz_selection/$grade")
                    }
                )
            }
        }
    }
}

// COMPOSABLES AUXILIARES

/**
 * Tarjeta interactiva para representar cada grado escolar.
 */
@Composable
fun GradeCard(grade: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick), // Hace que toda la tarjeta sea clickeable.
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Fondo suave para destacar.
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono del grado.
            Icon(
                Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))

            // Nombre del grado.
            Text(
                text = grade,
                style = MaterialTheme.typography.headlineSmall, // Fuente grande para fácil lectura.
                modifier = Modifier.weight(1f)
            )

            // Flecha de navegación.
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Acceder",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}