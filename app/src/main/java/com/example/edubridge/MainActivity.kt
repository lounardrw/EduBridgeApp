package com.example.edubridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.edubridge.data.SessionManager
import com.example.edubridge.ui.auth.LoginScreen
import com.example.edubridge.ui.student.StudentHomeScreen
import com.example.edubridge.ui.teacher.TeacherDashboardScreen
import com.example.edubridge.ui.theme.EduBridgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduBridgeTheme {

                val session = remember { SessionManager(this) }
                var screen by remember {
                    mutableStateOf(
                        when (session.getRol()) {
                            "ALUMNO" -> "student"
                            "PROFESOR" -> "teacher"
                            else -> "login"
                        }
                    )
                }

                when (screen) {
                    "login" -> LoginScreen(
                        onStudentLogin = { screen = "student" },
                        onTeacherLogin = { screen = "teacher" }
                    )
                    "student" -> StudentHomeScreen()
                    "teacher" -> TeacherDashboardScreen()
                }
            }
        }
    }
}
