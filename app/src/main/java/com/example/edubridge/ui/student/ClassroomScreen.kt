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
import androidx.navigation.NavController
import com.example.edubridge.Destinations

// Los grados de Secundaria (los únicos que se usan en la app).
val schoolGrades = listOf(
    "1° Secundaria",
    "2° Secundaria",
    "3° Secundaria"
)
@Composable
fun ClassroomsScreen(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Aulas Interactivas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // Ejemplo: Un botón para navegar al Quizz de 5to Grado
        Button(onClick = {
            // ¡AQUÍ ESTÁ LA LÓGICA DE NAVEGACIÓN QUE FALTABA!
            val grade = "5to Grado"
            navController.navigate("quiz_selection/$grade")
        }) {
            Text("Ir a Módulos de 5to Grado")
        }
    }
}