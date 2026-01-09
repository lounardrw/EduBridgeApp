package com.example.edubridge

import android.app.Application
import com.example.edubridge.data.local.db.AppDatabase
import com.google.firebase.FirebaseApp
import com.example.edubridge.data.PanicAlertRepository // Importamos el repositorio

class EduBridgeApp : Application() {

    // Se activa la inicialización de Room y se expone la propiedad 'database'
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        // Inicialización de Firebase (requerido por LoginViewModel)
        FirebaseApp.initializeApp(this)

        // FIX: Inicializamos el Repositorio de Pánico con su DAO de Room
        PanicAlertRepository.initialize(database.alertaDao())
    }
}