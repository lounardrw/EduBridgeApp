package com.example.edubridge.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val loading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = LoginUiState(loading = true)
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("ID no encontrado")

                val snapshot = db.collection("users").document(uid).get().await()
                if (!snapshot.exists()) throw Exception("Usuario no encontrado")

                val user = User(
                    id = uid,
                    matricula = snapshot.getString("matricula"),
                    nombre = snapshot.getString("nombre"),
                    correo = snapshot.getString("correo"),
                    rol = snapshot.getString("rol"),
                    // CAPTURA DE PROMEDIO REAL:
                    promedio = snapshot.getDouble("promedio") ?: 0.0
                )

                _state.value = LoginUiState(user = user)
            } catch (e: Exception) {
                _state.value = LoginUiState(error = "Credenciales incorrectas o error de red")
            }
        }
    }
}