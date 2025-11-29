package com.ecocoins.campus.data.model

data class Ticket(
    val id: String,
    val asunto: String,
    val descripcion: String,
    val categoria: CategoriaTicket,
    val prioridad: PrioridadTicket,
    val estado: EstadoTicket,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val respuestas: List<RespuestaTicket> = emptyList(),
    val usuarioId: String,
    val usuarioNombre: String
)

enum class CategoriaTicket {
    PROBLEMA_TECNICO,
    CONSULTA_ECOCOINS,
    PROBLEMA_CANJE,
    PROBLEMA_RECICLAJE,
    SUGERENCIA,
    OTRO
}

enum class PrioridadTicket {
    BAJA,
    MEDIA,
    ALTA,
    URGENTE
}

enum class EstadoTicket {
    ABIERTO,
    EN_PROCESO,
    RESUELTO,
    CERRADO
}

data class RespuestaTicket(
    val id: String,
    val mensaje: String,
    val fecha: String,
    val esAdmin: Boolean,
    val nombreUsuario: String
)

data class FAQ(
    val id: String,
    val pregunta: String,
    val respuesta: String,
    val categoria: CategoriaFAQ,
    val util: Int = 0
)

enum class CategoriaFAQ {
    CUENTA,
    RECICLAJE,
    ECOCOINS,
    CANJES,
    GENERAL
}

data class TicketRequest(
    val asunto: String,
    val descripcion: String,
    val categoria: CategoriaTicket,
    val prioridad: PrioridadTicket
)