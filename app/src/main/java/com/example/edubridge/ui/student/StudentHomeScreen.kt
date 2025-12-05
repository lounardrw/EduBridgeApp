package com.example.edubridge.ui.student

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

// 1. DEFINIMOS LAS PANTALLAS DE NAVEGACIÓN
// Esta es una clase sellada que nos permite manejar los estados de la pantalla de forma segura.
sealed class StudentScreen(val route: String, val title: String, val icon: ImageVector) {
    object Library : StudentScreen("library", "Biblioteca", Icons.Default.List)
    object Events : StudentScreen("events", "Eventos", Icons.Default.DateRange)
    object Classrooms : StudentScreen("classrooms", "Salones", Icons.Default.Home)
}

// Creamos una lista con las pantallas para el BottomBar
private val screens = listOf(
    StudentScreen.Classrooms,
    StudentScreen.Library,
    StudentScreen.Events,
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(onLogout: () -> Unit) {
    // 2. CREAMOS UNA VARIABLE PARA SABER EN QUÉ PANTALLA ESTAMOS
    // Esta variable 'currentScreen' recordará la pantalla actual. Inicia en 'Classrooms'.
    var currentScreen: StudentScreen by remember { mutableStateOf(StudentScreen.Classrooms) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) }, // El título cambia según la pantalla
                actions = {
                    // El botón de logout se mantiene igual
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
        // 4. EL 'when' AHORA FUNCIONA
        // Comparamos la variable 'currentScreen' para mostrar el contenido correcto.
        // Pasamos el modifier con el padding para que el contenido no se solape con las barras.
        val modifier = Modifier.padding(innerPadding)
        when (currentScreen) {
            is StudentScreen.Library -> LibraryScreen(modifier)
            is StudentScreen.Events -> EventsScreen(modifier)
            is StudentScreen.Classrooms -> ClassroomsScreen(modifier)
        }
    }
}


// --- PANTALLAS DE EJEMPLO ---
// Estas son las funciones que tu 'when' intentaba llamar.
// Si ya las tienes en otros archivos, asegúrate de importarlas.
// Si no las tienes, puedes usar estas como punto de partida.

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    // Aquí va el contenido de tu pantalla de Biblioteca
    Text("Contenido de Biblioteca", modifier = modifier)
}

@Composable
fun EventsScreen(modifier: Modifier = Modifier) {
    // Aquí va el contenido de tu pantalla de Eventos
    Text("Contenido de Eventos", modifier = modifier)
}

@Composable
fun ClassroomsScreen(modifier: Modifier = Modifier) {
    // Aquí va el contenido de tu pantalla de Salones
    Text("Contenido de Salones", modifier = modifier)
}

