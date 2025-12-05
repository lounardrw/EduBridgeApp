package com.example.edubridge
import android.app.Application
import com.google.firebase.FirebaseApp
//Carla Xochitl Cristalinas Maya
class EduBridgeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)


    }

}
