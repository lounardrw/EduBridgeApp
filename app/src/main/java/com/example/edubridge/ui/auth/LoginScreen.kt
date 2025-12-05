package com.example.edubridge.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.data.SessionManager

//Generación de la interfaz CARLA XOCHITL CRISTALINAS MAYA
@Composable
fun LoginScreen(
    onStudentLogin: () -> Unit,
    onTeacherLogin: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val uiState by vm.state.collectAsState()
    val ctx = LocalContext.current
    val session = remember { SessionManager(ctx) }

    LaunchedEffect(uiState.user) {
        uiState.user?.let { u ->
            session.saveUser(u.matricula, u.rol, u.nombre, u.correo)
            when (u.rol) {
                "ALUMNO" -> onStudentLogin()
                "PROFESOR" -> onTeacherLogin()
            }
        }
    }

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("EduBridge", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { vm.login(email.trim(), pass) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.loading) CircularProgressIndicator()

        uiState.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
