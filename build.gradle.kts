// ESTE ES EL CÓDIGO FINAL PARA el archivo build.gradle.kts DE LA RAÍZ

plugins {// Define el plugin de la aplicación Android
    alias(libs.plugins.android.application) apply false

    // Define el plugin de Kotlin para Android
    alias(libs.plugins.kotlin.android) apply false

    // Define el plugin de Hilt
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    // ✅ LA LÍNEA QUE FALTA: Define el plugin del Compilador de Compose
    alias(libs.plugins.kotlin.compose) apply false
}

