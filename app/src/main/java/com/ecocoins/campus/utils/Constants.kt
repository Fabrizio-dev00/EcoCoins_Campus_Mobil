package com.ecocoins.campus.utils

object Constants {
    // API Base URL - CAMBIAR A TU URL
    const val BASE_URL = "http://10.0.2.2:8080/" // Emulador Android
    // const val BASE_URL = "http://localhost:8080/" // Dispositivo físico con USB
    // const val BASE_URL = "http://TU_IP_LOCAL:8080/" // Dispositivo físico con WiFi

    // Endpoints
    const val AUTH_LOGIN = "api/auth/login"
    const val AUTH_REGISTER = "api/auth/register"

    // Timeout
    const val NETWORK_TIMEOUT = 60L

    // Material types
    val MATERIAL_TYPES = listOf(
        "PLASTICO",
        "PAPEL",
        "VIDRIO",
        "METAL",
        "ELECTRONICO",
        "ORGANICO"
    )

    // Status types
    object ReciclajeStatus {
        const val PENDIENTE = "PENDIENTE"
        const val VALIDADO = "VALIDADO"
        const val RECHAZADO = "RECHAZADO"
    }

    object CanjeStatus {
        const val PENDIENTE = "PENDIENTE"
        const val COMPLETADO = "COMPLETADO"
        const val CANCELADO = "CANCELADO"
    }

    object TicketStatus {
        const val ABIERTO = "ABIERTO"
        const val EN_PROCESO = "EN_PROCESO"
        const val RESUELTO = "RESUELTO"
        const val CERRADO = "CERRADO"
    }
}