package com.example.edubridge.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.R
import com.example.edubridge.data.SessionManager

@Composable
fun LoginScreen(
    onStudentLogin: (String) -> Unit,   // ⬅️ AHORA RECIBE EMAIL
    onTeacherLogin: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val uiState by vm.state.collectAsState()
    val ctx = LocalContext.current
    val session = remember { SessionManager(ctx) }

    // ─────────── LÓGICA DE LOGIN ───────────
    LaunchedEffect(uiState.user) {
        uiState.user?.let { u ->
            // Guardamos sesión
            session.saveUser(
                u.matricula,
                u.rol,
                u.nombre,
                u.correo
            )

            when (u.rol) {
                "ALUMNO" -> onStudentLogin(u.correo ?: "")   // ⬅️ AHORA SÍ MANDAMOS EMAIL
                "PROFESOR" -> onTeacherLogin()
            }
        }
    }

    // ─────────── CAMPOS DE TEXTO ───────────
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF2E7D32))
            )

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo EduBridge",
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = (-60).dp)
            )

            // CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-90).dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // TITULO
                    Text(
                        text = "Bienvenido a EDU BRIDGE",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // CORREO
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo institucional") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    // CONTRASEÑA
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation =
                            if (isPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible }
                            ) {
                                Icon(
                                    imageVector =
                                        if (isPasswordVisible) Icons.Filled.Visibility
                                        else Icons.Filled.VisibilityOff,
                                    contentDescription = "Mostrar contraseña"
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    // BOTÓN DE LOGIN
                    Button(
                        onClick = { vm.login(email.trim(), pass) },
                        enabled = !uiState.loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold)
                        }
                    }

                    // ERROR
                    uiState.error?.let {
                        Spacer(Modifier.height(12.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
