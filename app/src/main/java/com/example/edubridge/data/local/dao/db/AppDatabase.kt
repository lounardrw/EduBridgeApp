package com.example.edubridge.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.edubridge.data.local.dao.EventDao
import com.example.edubridge.data.local.dao.QuizDao
import com.example.edubridge.data.local.dao.ResourceDao
import com.example.edubridge.data.local.dao.AlertaDao
import com.example.edubridge.data.local.entitymodel.Event
import com.example.edubridge.data.local.entitymodel.QuizEntity
import com.example.edubridge.data.local.entitymodel.ResourceEntity
import com.example.edubridge.data.local.entitymodel.AlertaUbicacion

//BASE DE DATOS: AppDatabase

@Database(
    // Define las entidades que mapean las tablas de SQLite (Caché de todos los módulos)
    entities = [QuizEntity::class, ResourceEntity::class, Event::class, AlertaUbicacion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // DAOs: Interfaces de acceso a datos.
    abstract fun quizDao(): QuizDao
    abstract fun resourceDao(): ResourceDao
    abstract fun eventDao(): EventDao
    abstract fun alertaDao(): AlertaDao // <-- NUEVO

    companion object {
        private const val DATABASE_NAME = "EduBridgeDB"

        @Volatile
        private var INSTANCE: AppDatabase? = null
        // Asegura que solo se cree una instancia de la BD.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}