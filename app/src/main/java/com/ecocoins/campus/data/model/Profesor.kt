package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Profesor que ofrece recompensas en la tienda
 */
data class Profesor(
    @SerializedName("_id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("apellido")
    val apellido: String,

    @SerializedName("especialidad")
    val especialidad: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("fotoUrl")
    val fotoUrl: String? = null,

    @SerializedName("rating")
    val rating: Double = 0.0,

    @SerializedName("totalRecompensas")
    val totalRecompensas: Int = 0,

    @SerializedName("recompensas")
    val recompensas: List<RecompensaProfesor> = emptyList(),

    @SerializedName("activo")
    val activo: Boolean = true
) {
    fun getNombreCompleto(): String = "$nombre $apellido"

    fun getIniciales(): String {
        val nombreInicial = nombre.firstOrNull()?.uppercase() ?: ""
        val apellidoInicial = apellido.firstOrNull()?.uppercase() ?: ""
        return "$nombreInicial$apellidoInicial"
    }
}

/**
 * Recompensa específica ofrecida por un profesor
 */
data class RecompensaProfesor(
    @SerializedName("_id")
    val id: String,

    @SerializedName("tipo")
    val tipo: String, // "PUNTO_EXTRA", "QUITAR_TARDANZA", "TRABAJO_ADICIONAL", etc.

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("costoEcoCoins")
    val costoEcoCoins: Int,

    @SerializedName("stockDisponible")
    val stockDisponible: Int = -1, // -1 = ilimitado

    @SerializedName("activo")
    val activo: Boolean = true
) {
    fun getTipoFormateado(): String {
        return when (tipo) {
            "PUNTO_EXTRA" -> "Punto Extra"
            "QUITAR_TARDANZA" -> "Quitar Tardanza"
            "TRABAJO_ADICIONAL" -> "Trabajo Adicional"
            "RECUPERACION" -> "Recuperación"
            "EXAMEN_DIFERIDO" -> "Examen Diferido"
            else -> tipo
        }
    }

    fun getEmojiTipo(): String {
        return when (tipo) {
            "PUNTO_EXTRA" -> "📈"
            "QUITAR_TARDANZA" -> "⏰"
            "TRABAJO_ADICIONAL" -> "📝"
            "RECUPERACION" -> "🔄"
            "EXAMEN_DIFERIDO" -> "📅"
            else -> "🎁"
        }
    }
}

/**
 * Request para canjear una recompensa de profesor
 */
data class CanjearRecompensaProfesorRequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("profesorId")
    val profesorId: String,

    @SerializedName("recompensaId")
    val recompensaId: String,

    @SerializedName("observaciones")
    val observaciones: String? = null
)

/**
 * Response del canje de recompensa de profesor
 */
data class CanjearRecompensaProfesorResponse(
    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("canjeId")
    val canjeId: String,

    @SerializedName("profesor")
    val profesorNombre: String,

    @SerializedName("recompensa")
    val recompensaDescripcion: String,

    @SerializedName("costoEcoCoins")
    val costoEcoCoins: Int,

    @SerializedName("nuevoBalance")
    val nuevoBalance: Int,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("fechaCanje")
    val fechaCanje: String
)