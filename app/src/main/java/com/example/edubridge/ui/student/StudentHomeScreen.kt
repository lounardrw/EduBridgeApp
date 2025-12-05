package com.example.edubridge.ui.student

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.edubridge.data.PanicAlertRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth

data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Events : Screen("events")
    object Classrooms : Screen("classrooms")
}

@Composable fun LibraryScreen(modifier: Modifier = Modifier) { Text("Library Content", modifier) }
@Composable fun EventsScreen(modifier: Modifier = Modifier) { Text("Events Content", modifier) }
@Composable fun ClassroomsScreen(modifier: Modifier = Modifier) { Text("Classrooms Content", modifier) }

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun StudentHomeScreen(onLogout: () -> Unit = {}) {

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val auth = FirebaseAuth.getInstance()

    val navigationItems = listOf(
        NavItem("Library", Icons.Default.MenuBook, Screen.Library),
        NavItem("Events", Icons.Default.Event, Screen.Events),
        NavItem("Classrooms", Icons.Default.School, Screen.Classrooms)
    )

    var currentScreen: Screen by remember { mutableStateOf(Screen.Library) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    PanicAlertRepository.triggerAlert("Alumno de Prueba", latLng)
                    Toast.makeText(context, "¡Alerta enviada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "El permiso es necesario.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portal del Alumno") },
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        onLogout() // Permite regresar al login
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                navigationItems.forEach { item ->
                    IconButton(
                        onClick = { currentScreen = item.screen },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(item.icon, contentDescription = item.label)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Botón de Pánico")
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentScreen) {
            is Screen.Library -> LibraryScreen(modifier)
            is Screen.Events -> EventsScreen(modifier)
            is Screen.Classrooms -> ClassroomsScreen(modifier)
        }
    }
}
