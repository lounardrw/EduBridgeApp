package com.example.edubridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.edubridge.data.SessionManager
import com.example.edubridge.ui.auth.LoginScreen
import com.example.edubridge.ui.student.StudentHomeScreen
import com.example.edubridge.ui.teacher.TeacherDashboardScreen
import com.example.edubridge.ui.theme.EduBridgeTheme
import com.google.firebase.auth.FirebaseAuth // ¡Importación necesaria!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduBridgeTheme {

                val session = remember { SessionManager(this) }
                var screen by remember {
                    mutableStateOf(
                        // Lógica de arranque mejorada:
                        // Solo entra directo si está logueado Y tenemos un rol guardado.
                        if (session.isLogged()) {
                            when (session.getRol()) {
                                "ALUMNO" -> "student"
                                "PROFESOR" -> "teacher"
                                else -> "login" // Si hay error en el rol, va a login.
                            }
                        } else {
                            "login" // Si no está logueado, va a login.
                        }
                    )
                }

                when (screen) {
                    "login" -> LoginScreen(
                        // No se necesita hacer nada especial aquí.
                        // El LoginScreen ya actualiza la sesión por su cuenta.
                        onStudentLogin = { screen = "student" },
                        onTeacherLogin = { screen = "teacher" }
                    )

                    "student" -> StudentHomeScreen(
                        // ▼▼▼ ¡AQUÍ ESTÁ LA LÓGICA CLAVE! ▼▼▼
                        onLogout = {
                            // 1. Cierra la sesión de Firebase (si la usas para autenticar)
                            FirebaseAuth.getInstance().signOut()
                            // 2. Borra los datos locales de SharedPreferences
                            session.clear()
                            // 3. Navega a la pantalla de login
                            screen = "login"
                        }
                    )

                    "teacher" -> TeacherDashboardScreen(
                        // Lo mismo para el profesor
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            session.clear()
                            screen = "login"
                        }
                    )
                }
            }
        }
    }
}
