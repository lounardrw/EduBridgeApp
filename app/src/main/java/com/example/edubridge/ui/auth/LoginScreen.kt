package com.example.edubridge.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edubridge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onStudentLogin: (String) -> Unit,
    onTeacherLogin: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val uiState by vm.state.collectAsState()

    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFFE8F5E9)

    LaunchedEffect(uiState.user) {
        uiState.user?.let { u ->
            when (u.rol) {
                "ALUMNO" -> onStudentLogin(u.correo ?: "")
                "PROFESOR" -> onTeacherLogin()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(lightGreen)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Brush.verticalGradient(listOf(primaryGreen, Color(0xFF1B5E20))))
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(130.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.a),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(18.dp).clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // CENTRALIZACIÓN
                ) {
                    Text(
                        "EduBridge",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black, // Más grueso
                        color = primaryGreen,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "CONECTANDO TU FUTURO ESCOLAR",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Institucional") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { vm.login(email.trim(), pass) },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                        enabled = !uiState.loading
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("INICIAR SESIÓN", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        }
                    }

                    uiState.error?.let {
                        Text(it, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }
        }
    }
}