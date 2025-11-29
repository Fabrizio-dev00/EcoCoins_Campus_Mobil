package com.ecocoins.campus.data.model

data class Logro(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val icono: String,
    val categoria: CategoriaLogro,
    val objetivo: Int,
    val progreso: Int,
    val desbloqueado: Boolean,
    val fechaDesbloqueo: String? = null,
    val recompensaEcoCoins: Int,
    val rareza: RarezaLogro
)

enum class CategoriaLogro {
    RECICLAJE,
    ECOCOINS,
    SOCIAL,
    RACHA,
    ESPECIAL
}

enum class RarezaLogro {
    COMUN,
    RARO,
    EPICO,
    LEGENDARIO
}

data class LogrosResponse(
    val logros: List<Logro>,
    val totalDesbloqueados: Int,
    val totalLogros: Int,
    val porcentajeCompletado: Int
)

data class CategoriaLogros(
    val categoria: CategoriaLogro,
    val logros: List<Logro>,
    val completados: Int,
    val total: Int
)