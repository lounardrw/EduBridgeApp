package com.example.edubridge.ui.teacher

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// 1. DEFINIMOS LAS PANTALLAS DE NAVEGACIÓN PARA EL PROFESOR
sealed class TeacherScreen(val route: String, val title: String, val icon: ImageVector) {
    object ManageLibrary : TeacherScreen("manage_library", "Biblioteca", Icons.Default.Book)
    object ManageEvents : TeacherScreen("manage_events", "Eventos", Icons.Default.Event)
    object ManageQuizzes : TeacherScreen("manage_quizzes", "Quizzes", Icons.Default.Quiz)
    object AlertMap : TeacherScreen("alert_map", "Alertas", Icons.Default.NotificationImportant)
}

// Creamos la lista de pantallas para la barra de navegación
private val screens = listOf(
    TeacherScreen.ManageLibrary,
    TeacherScreen.ManageEvents,
    TeacherScreen.ManageQuizzes,
    TeacherScreen.AlertMap
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(onLogout: () -> Unit) {
    // 2. CREAMOS UNA VARIABLE PARA SABER EN QUÉ PANTALLA ESTAMOS
    var currentScreen: TeacherScreen by remember { mutableStateOf(TeacherScreen.ManageLibrary) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) }, // El título cambia dinámicamente
                actions = {
                    // El botón de logout se mantiene
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // 3. CREAMOS LA BARRA DE NAVEGACIÓN INFERIOR
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen } // Al hacer clic, cambiamos la pantalla
                    )
                }
            }
        }
    ) { innerPadding ->
        // 4. EL 'when' AHORA MUESTRA LA PANTALLA CORRECTA
        val modifier = Modifier.padding(innerPadding)
        when (currentScreen) {
            is TeacherScreen.ManageLibrary -> ManageLibraryScreen(modifier)
            is TeacherScreen.ManageEvents -> ManageEventsScreen(modifier)
            is TeacherScreen.ManageQuizzes -> ManageQuizzesScreen(modifier)
            is TeacherScreen.AlertMap -> AlertMapScreen(modifier)
        }
    }
}

// --- PANTALLAS DE EJEMPLO PARA LAS SECCIONES DEL PROFESOR ---
// Puedes mover estas funciones a sus propios archivos más adelante si lo deseas.

@Composable
fun ManageLibraryScreen(modifier: Modifier = Modifier) {
    // Aquí irá el contenido para gestionar los libros (CRUD)
    Text(text = "Panel de Gestión de Biblioteca", modifier = modifier)
}

@Composable
fun ManageEventsScreen(modifier: Modifier = Modifier) {
    // Aquí irá el contenido para gestionar los eventos del calendario (CRUD)
    Text(text = "Panel de Gestión de Eventos", modifier = modifier)
}

@Composable
fun ManageQuizzesScreen(modifier: Modifier = Modifier) {
    // Aquí irá el contenido para gestionar los quizzes (CRUD)
    Text(text = "Panel de Gestión de Quizzes", modifier = modifier)
}

@Composable
fun AlertMapScreen(modifier: Modifier = Modifier) {
    // Aquí irá el contenido para visualizar y validar las alertas
    Text(text = "Mapa de Alertas", modifier = modifier)
}
