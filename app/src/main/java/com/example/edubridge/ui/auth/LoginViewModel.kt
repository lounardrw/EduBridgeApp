package com.example.edubridge.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edubridge.data.model.User
import com.example.edubridge.data.model.UserResponse
import com.example.edubridge.data.remote.LoginRequest
import com.example.edubridge.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
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

    private val _state = MutableStateFlow(LoginUiState())
        get() = field
    val state: StateFlow<LoginUiState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = LoginUiState(loading = true)

                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("UID no encontrado")

                val req = LoginRequest(firebase_uid = uid)
                val response = RetrofitClient.api.login(req)

                if (!response.isSuccessful)
                    throw Exception("Error servidor ${response.code()}")

                val body: UserResponse = response.body()
                    ?: throw Exception("Respuesta vacía")

                if (!body.ok)
                    throw Exception(body.error ?: "Acceso denegado")

                val user = User(
                    id = body.id ?: "",
                    matricula = body.matricula,
                    nombre = body.nombre,
                    correo = body.correo,
                    rol = body.rol
                )

                _state.value = LoginUiState(user = user)

            } catch (e: Exception) {
                _state.value = LoginUiState(error = e.message)
            }
        }
    }
}
