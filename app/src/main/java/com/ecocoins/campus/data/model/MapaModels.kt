package com.ecocoins.campus.data.model

data class PuntoReciclaje(
    val id: String,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val tipo: TipoPuntoReciclaje,
    val materialesAceptados: List<String>,
    val horario: String,
    val distanciaKm: Double = 0.0,
    val telefono: String? = null,
    val estado: EstadoPunto = EstadoPunto.ABIERTO,
    val capacidadActual: Int = 0, // 0-100%
    val ultimaActualizacion: String? = null
)

enum class TipoPuntoReciclaje {
    CONTENEDOR,
    CENTRO_ACOPIO,
    PUNTO_LIMPIO,
    UNIVERSIDAD
}

enum class EstadoPunto {
    ABIERTO,
    CERRADO,
    LLENO
}

data class FiltroMapa(
    val tiposPunto: List<TipoPuntoReciclaje> = TipoPuntoReciclaje.values().toList(),
    val materiales: List<String> = emptyList(),
    val soloAbiertos: Boolean = false,
    val radioMaxKm: Double = 5.0
)