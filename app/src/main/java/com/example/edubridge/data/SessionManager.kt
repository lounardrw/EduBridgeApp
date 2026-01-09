package com.example.edubridge.data

import android.content.Context

class SessionManager(ctx: Context) {

    //Uso de Shared Preference :))
    private val prefs = ctx.getSharedPreferences("edubridge", Context.MODE_PRIVATE)

    fun saveUser(matricula: String?, rol: String?, nombre: String?, correo: String?) {
        prefs.edit().apply {
            putBoolean("logged", true)
            putString("matricula", matricula)
            putString("rol", rol)
            putString("nombre", nombre)
            putString("correo", correo)
            apply()
        }
    }

    fun getRol(): String? = prefs.getString("rol", null)
    fun isLogged() = prefs.getBoolean("logged", false)
    fun clear() {
        // Forma para borrar los datos
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}