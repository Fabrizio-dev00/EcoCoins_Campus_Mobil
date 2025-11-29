package com.ecocoins.campus.data.model

data class Notificacion(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val tipo: TipoNotificacion,
    val fecha: String,
    val leida: Boolean = false,
    val iconoUrl: String? = null,
    val accionUrl: String? = null,
    val metadata: Map<String, String>? = null
)

enum class TipoNotificacion {
    CANJE_LISTO,
    NUEVA_RECOMPENSA,
    LOGRO_DESBLOQUEADO,
    RECORDATORIO,
    SISTEMA,
    SOCIAL
}

data class NotificacionesResponse(
    val notificaciones: List<Notificacion>,
    val noLeidas: Int,
    val total: Int
)