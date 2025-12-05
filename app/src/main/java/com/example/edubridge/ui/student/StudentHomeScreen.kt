package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.edubridge.ui.student.ClassroomsScreen
import com.example.edubridge.ui.student.EventsScreen
import com.example.edubridge.ui.student.LibraryScreen

// ====================================================================
// PANTALLAS Y DEFINICIONES MINIMALES
// NOTA: Se eliminó toda la lógica de FAB, Drawer y Permisos para evitar conflictos
// con la implementación de Karen y enfocarse solo en la navegación (Luis).
// ====================================================================

// Definiciones de pestañas (NECESARIAS para el NavHost)
sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Events : Screen("events")
    object Classrooms : Screen("classrooms")
}

// Stubs (funciones vacías) que evitan errores de referencia
@Composable
fun EventsScreen(modifier: Modifier = Modifier) { /* Contenido de Montse */ }
@Composable
fun LibraryScreen(modifier: Modifier = Modifier) { /* Contenido de Isaac */ }


// ----------------------------------------------------
// FUNCIONES DE ENTRADA (MANDATORIAS PARA MAINACTIVITY.KT)
// ----------------------------------------------------

/**
 * Sobrecarga básica (para el caso donde no se pasa el NavController).
 * Tarea: Asegurar que la función simple compile.
 */
@Composable
fun StudentHomeScreen() {
    StudentHomeScreen(email = "temporal@edubridge.com", navController = null)
}

/**
 * Sobrecarga con NavController (MANDATORIA para MainActivity.kt).
 * Tarea: Recibir el NavController desde el NavHost.
 */
@Composable
fun StudentHomeScreen(navController: NavController) {
    StudentHomeScreen(email = "temporal@edubridge.com", navController = navController)
}

@Composable
// IMPLEMENTACIÓN PRINCIPAL (Firma de Karen + NavController de Luis)
fun StudentHomeScreen(email: String, navController: NavController?) {
    // ESTE ES EL CUERPO PRINCIPAL DEL CÓDIGO

    val isNavAvailable = navController != null

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // ZONA DE CONFLICTO: El código completo del Scaffold de KAREN iría aquí.

        // Ejemplo mínimo para mostrar la pantalla de Aulas (tu módulo) si la navegación funciona.
        if (isNavAvailable) {
            ClassroomsScreen(navController = navController!!)
        } else {
            Text("PORTAL DEL ALUMNO (UI de Karen Pendiente)", style = MaterialTheme.typography.titleLarge)
        }
    }
    // NOTA: Los composables auxiliares (Drawer, Sheets) que implementaste para Karen fueron eliminados
    // para que ella pueda subirlos sin conflicto.
}