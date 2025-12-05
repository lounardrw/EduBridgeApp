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

data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Events : Screen("events")
    object Classrooms : Screen("classrooms")
}

// --- PASO 1: ELIMINA ESTAS FUNCIONES DUMMY ---
// @Composable fun LibraryScreen(modifier: Modifier = Modifier) { Text("Library Content", modifier) } <-- ELIMINAR
@Composable fun EventsScreen(modifier: Modifier = Modifier) { Text("Events Content", modifier) }
@Composable fun ClassroomsScreen(modifier: Modifier = Modifier) { Text("Classrooms Content", modifier) }

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun StudentHomeScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val navigationItems = listOf(
        NavItem(label = "Library", icon = Icons.Default.MenuBook, screen = Screen.Library),
        NavItem(label = "Events", icon = Icons.Default.Event, screen = Screen.Events),
        NavItem(label = "Classrooms", icon = Icons.Default.School, screen = Screen.Classrooms)
    )

    var currentScreen: Screen by remember { mutableStateOf<Screen>(Screen.Library) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    Log.d("PanicButton", "Ubicación obtenida: $latLng")
                    PanicAlertRepository.triggerAlert("Alumno de Prueba", latLng)
                    Toast.makeText(context, "¡Alerta enviada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "El permiso de ubicación es necesario para el botón de pánico.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Portal del Alumno") })
        },
        bottomBar = {
            BottomAppBar {
                navigationItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentScreen == item.screen,
                        onClick = { currentScreen = item.screen },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
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

        // --- PASO 2: ASEGÚRATE DE QUE EL WHEN LLAME A LA FUNCIÓN CORRECTA ---
        when (currentScreen) {
            // Esta llamada ahora usará la función real de LibraryScreen.kt
            is Screen.Library -> LibraryScreen(modifier = modifier)
            is Screen.Events -> EventsScreen(modifier = modifier)
            is Screen.Classrooms -> ClassroomsScreen(modifier = modifier)
        }
    }
}
