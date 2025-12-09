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

// IMPORTS PARA LAS PANTALLAS DEL PROFESOR Y ALUMNO
import com.example.edubridge.ui.student.QuizSelectionScreen
import com.example.edubridge.ui.teacher.ManageQuizzesScreen
import com.example.edubridge.ui.teacher.AlertMapScreen
import com.example.edubridge.ui.teacher.ManageEventsScreen
import com.example.edubridge.ui.teacher.ManageLibraryScreen


object Destinations {
    const val LOGIN_ROUTE = "login"
    // Versión de 'main', que es la correcta para pasar el email
    const val STUDENT_HOME_ROUTE = "student_home/{email}"
    const val TEACHER_DASHBOARD_ROUTE = "teacher_dashboard"

    // Rutas de gestión del profesor
    const val MANAGE_LIBRARY_ROUTE = "manage_library"
    const val MANAGE_EVENTS_ROUTE = "manage_events"
    const val MANAGE_QUIZZES_ROUTE = "manage_quizzes"
    const val ALERT_MAP_ROUTE = "alert_map"

    // Ruta para la selección de Quizz de la rama de Cuenca
    const val QUIZ_SELECTION_ROUTE = "quiz_selection/{grade}"

    // Función de ayuda de 'main' para construir la ruta del alumno
    fun studentHomeWithEmail(email: String) = "student_home/$email"
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
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN_ROUTE
    ) {

        // --- Pantalla de Inicio de Sesión ---
        composable(Destinations.LOGIN_ROUTE) {
            LoginScreen(
                // Lógica de 'main': onStudentLogin ahora recibe el email
                onStudentLogin = { email ->
                    navController.navigate(Destinations.studentHomeWithEmail(email)) {
                        popUpTo(Destinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onTeacherLogin = {
                    navController.navigate(Destinations.TEACHER_DASHBOARD_ROUTE) {
                        popUpTo(Destinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // --- Pantalla Principal del Alumno ---
        // Lógica de 'main': Se define el argumento 'email' y se extrae
        composable(
            route = Destinations.STUDENT_HOME_ROUTE,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            if (email != null) {
                // Ahora le pasamos el navController a StudentHomeScreen
                StudentHomeScreen(email = email, navController = navController) // <-- ACTUALIZA ESTA LÍNEA
            } else {
                // Medida de seguridad: si el email es nulo, vuelve al login
                navController.popBackStack(Destinations.LOGIN_ROUTE, inclusive = false)
            }
        }

        // --- Panel de Control del Profesor ---
        composable(Destinations.TEACHER_DASHBOARD_ROUTE) {
            TeacherDashboardScreen(
                onManageLibrary = { navController.navigate(Destinations.MANAGE_LIBRARY_ROUTE) },
                onManageEvents = { navController.navigate(Destinations.MANAGE_EVENTS_ROUTE) },
                onManageQuizzes = { navController.navigate(Destinations.MANAGE_QUIZZES_ROUTE) },
                onViewAlert = { navController.navigate(Destinations.ALERT_MAP_ROUTE) }
            )
        }

        // --- Nuevas Pantallas de Gestión del Profesor ---
        composable(Destinations.MANAGE_QUIZZES_ROUTE) {
            ManageQuizzesScreen()
        }
        composable(Destinations.ALERT_MAP_ROUTE) {
            AlertMapScreen(onDismiss = { navController.popBackStack() })
        }
        composable(Destinations.MANAGE_LIBRARY_ROUTE) {
            ManageLibraryScreen()
        }
        composable(Destinations.MANAGE_EVENTS_ROUTE) {
            ManageEventsScreen()
        }

        // --- Nueva Pantalla de Selección de Quizz (de la rama de Cuenca) ---
        composable(
            route = Destinations.QUIZ_SELECTION_ROUTE,
            arguments = listOf(navArgument("grade") { type = NavType.StringType })
        ) { backStackEntry ->
            val grade = backStackEntry.arguments?.getString("grade") ?: "Grado Desconocido"
            QuizSelectionScreen(grade = grade)
        }
    }
}
