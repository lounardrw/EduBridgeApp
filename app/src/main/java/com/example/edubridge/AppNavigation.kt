package com.example.edubridge

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.edubridge.ui.auth.LoginScreen
import com.example.edubridge.ui.student.EventsScreen
import com.example.edubridge.ui.student.StudentHomeScreen
import com.example.edubridge.ui.teacher.TeacherDashboardScreen

object Destinations {
    const val LOGIN_ROUTE = "login"
    const val STUDENT_HOME_ROUTE = "student_home"
    const val TEACHER_DASHBOARD_ROUTE = "teacher_dashboard"
    const val EVENTS_ROUTE = "events"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN_ROUTE
    ) {
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

        composable(Destinations.STUDENT_HOME_ROUTE) {
            StudentHomeScreen(
                onNavigateToEvents = {
                    navController.navigate(Destinations.EVENTS_ROUTE)
                }
            )
        }

        composable(Destinations.TEACHER_DASHBOARD_ROUTE) {
            TeacherDashboardScreen()
        }

        composable(Destinations.EVENTS_ROUTE) {
            EventsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}


