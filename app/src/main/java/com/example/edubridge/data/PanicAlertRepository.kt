package com.example.edubridge.data

import com.google.android.gms.maps.model.LatLng

object PanicAlertRepository {
    fun triggerAlert(nombre: String, location: LatLng) {
        // Aquí puedes poner lógica real o solo un log para pruebas
        println("Alerta enviada por $nombre en ubicación: $location")
    }
}

