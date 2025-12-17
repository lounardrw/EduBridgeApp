plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // Aplicamos KSP con su ID directo y versión para máxima seguridad (1.9.24)
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false

    // Dejamos este ID para que el proyecto lo encuentre (Firebase)
    id("com.google.gms.google-services") version "4.4.4" apply false
}