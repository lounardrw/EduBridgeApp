package com.example.edubridge.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.edubridge.R // Asegúrate de que esta importación exista

@Composable
fun LoginScreen(
    onStudentLogin: (String) -> Unit,
    onTeacherLogin: () -> Unit
) {
    // Estados para guardar lo que el usuario escribe
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Columna principal que centra todo verticalmente y añade espaciado
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp), // Añade padding a los lados
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Logo de la aplicación
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Asegúrate de tener un logo aquí
            contentDescription = "Logo de EduBridge",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 2. Campo de texto para el Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 3. Campo de texto para la Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(), // Oculta la contraseña
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 4. Botón para Iniciar Sesión como Alumno
        Button(
            onClick = {
                // Lógica de validación y navegación para el alumno
                if (email.isNotBlank() && password.isNotBlank()) {
                    // Aquí iría tu validación real contra Firebase o tu API
                    // Si es exitoso, navegas y pasas el email.
                    onStudentLogin(email)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión como Alumno")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 5. Botón para Iniciar Sesión como Profesor
        Button(
            onClick = {
                // Lógica de validación y navegación para el profesor
                if (email.isNotBlank() && password.isNotBlank()) {
                    // Aquí iría tu validación real
                    onTeacherLogin()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión como Profesor")
        }
    }
}
