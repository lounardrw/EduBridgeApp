package com.example.edubridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.edubridge.ui.auth.LoginScreen
import com.example.edubridge.ui.auth.RegisterScreen
import com.example.edubridge.ui.student.StudentHomeScreen
import com.example.edubridge.ui.teacher.TeacherDashboardScreen
import com.example.edubridge.ui.student.QuizSelectionScreen
import com.example.edubridge.ui.teacher.ManageQuizzesScreen
import com.example.edubridge.ui.teacher.AlertMapScreen
import com.example.edubridge.ui.teacher.ManageEventsScreen
import com.example.edubridge.ui.teacher.ManageLibraryScreen
import com.example.edubridge.ui.student.QuizModuleDetailScreen
// FIX: Asegúrate de que esta importación coincida con la ubicación real de tu SessionManager
import com.example.edubridge.data.SessionManager

object Destinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"

    const val STUDENT_HOME_ROUTE = "student_home/{email}"
    const val TEACHER_DASHBOARD_ROUTE = "teacher_dashboard"

    const val MANAGE_LIBRARY_ROUTE = "manage_library"
    const val MANAGE_EVENTS_ROUTE = "manage_events"
    const val MANAGE_QUIZZES_ROUTE = "manage_quizzes"
    const val ALERT_MAP_ROUTE = "alert_map"

    const val QUIZ_SELECTION_ROUTE = "quiz_selection/{grade}"
    const val QUIZ_DETAIL_ROUTE = "quiz_detail/{quizId}"

    fun studentHomeWithEmail(email: String) = "student_home/$email"
    fun quizDetailWithId(quizId: Int) = "quiz_detail/$quizId"
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
    val context = LocalContext.current
    // Inicializamos el SessionManager
    val sessionManager = remember { SessionManager(context) }

    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN_ROUTE
    ) {

        composable(Destinations.LOGIN_ROUTE) {
            LoginScreen(
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

        composable(Destinations.REGISTER_ROUTE) {
            RegisterScreen(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(
            route = Destinations.STUDENT_HOME_ROUTE,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            StudentHomeScreen(
                email = email,
                navController = navController
            )
        }

        composable(Destinations.TEACHER_DASHBOARD_ROUTE) {
            TeacherDashboardScreen(
                onManageLibrary = { navController.navigate(Destinations.MANAGE_LIBRARY_ROUTE) },
                onManageEvents = { navController.navigate(Destinations.MANAGE_EVENTS_ROUTE) },
                onManageQuizzes = { navController.navigate(Destinations.MANAGE_QUIZZES_ROUTE) },
                onViewAlert = { navController.navigate(Destinations.ALERT_MAP_ROUTE) },
                onLogout = {
                    // FIX: Cambiado de clearSession() a clear() para coincidir con tu SessionManager.kt
                    sessionManager.clear()
                    navController.navigate(Destinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

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

        composable(
            route = Destinations.QUIZ_SELECTION_ROUTE,
            arguments = listOf(navArgument("grade") { type = NavType.StringType })
        ) { backStackEntry ->
            val grade = backStackEntry.arguments?.getString("grade") ?: "Grado Desconocido"
            QuizSelectionScreen(
                grade = grade,
                onModuleSelected = { quizId ->
                    navController.navigate(Destinations.quizDetailWithId(quizId))
                }
            )
        }

        composable(
            route = Destinations.QUIZ_DETAIL_ROUTE,
            arguments = listOf(navArgument("quizId") { type = NavType.IntType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getInt("quizId")
            if (quizId != null && quizId > 0) {
                QuizModuleDetailScreen(quizId = quizId)
            } else {
                navController.popBackStack()
            }
        }
    }
}