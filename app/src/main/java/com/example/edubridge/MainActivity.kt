package com.example.edubridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.edubridge.ui.auth.LoginScreen
import com.example.edubridge.ui.student.StudentHomeScreen
import com.example.edubridge.ui.teacher.*

// Objeto para mantener las rutas centralizadas
object Destinations {
    const val LOGIN_ROUTE = "login"
    const val STUDENT_HOME_ROUTE = "student_home"
    const val TEACHER_DASHBOARD_ROUTE = "teacher_dashboard"

    // Nuevas rutas para la gestión del profesor
    const val MANAGE_LIBRARY_ROUTE = "manage_library"
    const val MANAGE_EVENTS_ROUTE = "manage_events"
    const val MANAGE_QUIZZES_ROUTE = "manage_quizzes"
    const val ALERT_MAP_ROUTE = "alert_map"
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
                onStudentLogin = {
                    navController.navigate(Destinations.STUDENT_HOME_ROUTE) {
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
        composable(Destinations.STUDENT_HOME_ROUTE) {
            StudentHomeScreen()
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

        composable(Destinations.MANAGE_LIBRARY_ROUTE) {
            ManageLibraryScreen()
        }
        composable(Destinations.MANAGE_EVENTS_ROUTE) {
            ManageEventsScreen()
        }
        composable(Destinations.MANAGE_QUIZZES_ROUTE) {
            ManageQuizzesScreen()
        }
        composable(Destinations.ALERT_MAP_ROUTE) {
            AlertMapScreen(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
