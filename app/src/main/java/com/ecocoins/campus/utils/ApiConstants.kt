package com.ecocoins.campus.utils

object ApiConstants {
    // ========== BASE URL ==========
    // DESARROLLO (Emulador Android)
    const val BASE_URL = "http://10.0.2.2:8080/"

    // DESARROLLO (Dispositivo físico en misma red)
    // const val BASE_URL = "http://192.168.1.XXX:8080/"  // Cambia por tu IP local

    // PRODUCCIÓN (cuando despliegues)
    // const val BASE_URL = "https://api.ecocoins.com/"

    // ========== CONFIGURACIÓN ==========
    const val CONNECT_TIMEOUT = 30L // segundos
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}