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
import com.example.edubridge.R // Asumiendo que tu recurso R es accesible
import com.example.edubridge.data.SessionManager

// Generación de la interfaz CARLA XOCHITL CRISTALINAS MAYA
@Composable
fun LoginScreen(
    onStudentLogin: () -> Unit,
    onTeacherLogin: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val uiState by vm.state.collectAsState()
    val ctx = LocalContext.current
    // Es mejor usar 'remember' fuera de LaunchedEffect si el objeto es costoso o debe persistir
    val session = remember { SessionManager(ctx) }

    // --- LÓGICA DE NAVEGACIÓN Y SESIÓN (Tu lógica original) ---
    LaunchedEffect(uiState.user) {
        uiState.user?.let { u ->
            session.saveUser(u.matricula, u.rol, u.nombre, u.correo)
            when (u.rol) {
                "ALUMNO" -> onStudentLogin()
                "PROFESOR" -> onTeacherLogin()
            }
        }
    }

    // --- ESTADOS DE LOS CAMPOS DE TEXTO (Tu lógica original) ---
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) } // Nuevo para el icono de visibilidad

    // --- APLICACIÓN DEL NUEVO DISEÑO ---
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5) // Fondo general (similar al XML)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header (Vista Verde)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF2E7D32)) // Color verde
            )

            // 2. Logo (Posicionado sobre el Header)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con tu logo
                contentDescription = "Logo de EduBridge",
                modifier = Modifier
                    .size(120.dp)
                    // Offset: Mueve el logo para que esté centrado verticalmente con el borde superior de la tarjeta
                    .offset(y = (-60).dp)
            )

            // 3. CardView (Contenedor del Formulario)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    // Offset: Mueve la tarjeta hacia arriba, cubriendo parte del logo y el header
                    .offset(y = (-90).dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título de Bienvenida
                    Text(
                        text = "Bienvenido a EDU BRIDGE",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Campo de Correo
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo institucional") },
                        modifier = Modifier.fillMaxWidth(),
                        // Estilo "OutlinedBox" es el predeterminado en OutlinedTextField de Material 3
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Espacio entre campos

                    // Campo de Contraseña
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        // Toggle de visibilidad de contraseña
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(icon, contentDescription = "Toggle password visibility")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de Inicio de Sesión
                    Button(
                        onClick = { vm.login(email.trim(), pass) },
                        enabled = !uiState.loading, // Deshabilitar si está cargando
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Color verde
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Mensajes de error
                    uiState.error?.let {
                        Spacer(Modifier.height(12.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}