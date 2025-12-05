package com.example.edubridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // Importación necesaria para pasar argumentos
import com.example.edubridge.ui.auth.LoginScreen
import com.example.edubridge.ui.student.StudentHomeScreen
import com.example.edubridge.ui.teacher.TeacherDashboardScreen

// NUEVOS IMPORTS para las pantallas del Profesor y Alumno
import com.example.edubridge.ui.student.QuizSelectionScreen // Importación necesaria (asumiendo que ya creaste el archivo)
import com.example.edubridge.ui.teacher.ManageQuizzesScreen
import com.example.edubridge.ui.teacher.AlertMapScreen
import com.example.edubridge.ui.teacher.ManageEventsScreen
import com.example.edubridge.ui.teacher.ManageLibraryScreen


object Destinations {
    const val LOGIN_ROUTE = "login"
    const val STUDENT_HOME_ROUTE = "student_home"
    const val TEACHER_DASHBOARD_ROUTE = "teacher_dashboard"

    // RUTAS AÑADIDAS para la gestión del profesor (LUIS, ISAAC, MONTSÉ)
    const val MANAGE_LIBRARY_ROUTE = "manage_library"
    const val MANAGE_EVENTS_ROUTE = "manage_events"
    const val MANAGE_QUIZZES_ROUTE = "manage_quizzes" // TAREA DE LUIS
    const val ALERT_MAP_ROUTE = "alert_map" // TAREA DE LUIS

    // RUTA AÑADIDA PARA EL QUIZZ (con argumento de grado)
    const val QUIZ_SELECTION_ROUTE = "quiz_selection/{grade}"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // El NavController es el cerebro de la navegación. Recuerda el estado y las rutas.
    val navController = rememberNavController()

    // NavHost es el contenedor que intercambia las pantallas (destinos).
    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN_ROUTE // La app siempre empieza en el Login.
    ) {

        // --- Pantalla de Inicio de Sesión ---
        composable(Destinations.LOGIN_ROUTE) {
            LoginScreen(
                onStudentLogin = {
                    // Navega a la pantalla de alumno y limpia la pila de navegación
                    navController.navigate(Destinations.STUDENT_HOME_ROUTE) {
                        popUpTo(Destinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onTeacherLogin = {
                    // Navega a la pantalla de profesor y limpia la pila.
                    navController.navigate(Destinations.TEACHER_DASHBOARD_ROUTE) {
                        popUpTo(Destinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // --- Pantalla Principal del Alumno ---
        composable(Destinations.STUDENT_HOME_ROUTE) {
            StudentHomeScreen(
                // Pasamos el NavController para que las pestañas puedan navegar (en este caso Aulas)
                // Se utiliza una sobrecarga de ClassroomsScreen para pasar el NavController
                navController = navController
            )
        }

        // --- Panel de Control del Profesor (TeacherDashboard) ---
        composable(Destinations.TEACHER_DASHBOARD_ROUTE) {
            // CORRECCIÓN CLAVE: Pasamos los lambdas de navegación a la función
            TeacherDashboardScreen(
                onManageLibrary = { navController.navigate(Destinations.MANAGE_LIBRARY_ROUTE) },
                onManageEvents = { navController.navigate(Destinations.MANAGE_EVENTS_ROUTE) },
                onManageQuizzes = { navController.navigate(Destinations.MANAGE_QUIZZES_ROUTE) }, // LUIS
                onViewAlert = { navController.navigate(Destinations.ALERT_MAP_ROUTE) } // LUIS
            )
        }

        // --- Nuevas Pantallas de Gestión del Profesor ---

        // TAREA DE LUIS: Gestión de Cuestionarios
        composable(Destinations.MANAGE_QUIZZES_ROUTE) {
            ManageQuizzesScreen()
        }

        // TAREA DE LUIS: Mapa de Alertas de Pánico
        composable(Destinations.ALERT_MAP_ROUTE) {
            // Pasamos una función para que la pantalla pueda volver al dashboard
            AlertMapScreen(onDismiss = { navController.popBackStack() })
        }

        // Gestión de Biblioteca (ISAAC)
        composable(Destinations.MANAGE_LIBRARY_ROUTE) {
            // Stub temporal, asume que ManageLibraryScreen existe
            ManageLibraryScreen()
        }

        // Gestión de Eventos (MONTSÉ)
        composable(Destinations.MANAGE_EVENTS_ROUTE) {
            // Stub temporal, asume que ManageEventsScreen existe
            ManageEventsScreen()
        }

        // --- Nueva Pantalla de Selección de Quizz ---
        // TAREA DE LUIS: Pantalla de selección de módulos después de elegir el grado
        composable(
            route = Destinations.QUIZ_SELECTION_ROUTE,
            arguments = listOf(navArgument("grade") { type = NavType.StringType })
        ) { backStackEntry ->
            val grade = backStackEntry.arguments?.getString("grade") ?: "Grado Desconocido"
            QuizSelectionScreen(grade = grade)
        }
    }
}