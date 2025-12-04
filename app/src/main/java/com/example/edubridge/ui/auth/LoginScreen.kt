package com.example.edubridge.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun LoginScreen(
    onStudentLogin: () -> Unit,
    onTeacherLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("EduBridge", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStudentLogin) {
            Text("Entrar como Alumno")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onTeacherLogin) {
            Text("Entrar como Profesor")
        }
    }
}
