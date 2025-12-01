package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo para la información completa de referidos de un usuario
 */
data class ReferidosInfo(
    @SerializedName("codigoReferido")
    val codigoReferido: String = "",

    @SerializedName("totalReferidos")
    val totalReferidos: Int = 0,

    @SerializedName("totalEcoCoinsGanados")
    val totalEcoCoinsGanados: Int = 0,

    @SerializedName("referidos")
    val referidos: List<ReferidoDetalle> = emptyList()
)

/**
 * Modelo para el detalle de un referido
 */
data class ReferidoDetalle(
    @SerializedName("_id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("fechaRegistro")
    val fechaRegistro: String,

    @SerializedName("activo")
    val activo: Boolean = true,

    @SerializedName("ecoCoinsGanados")
    val ecoCoinsGanados: Int = 50
)
/**
 * Modelo para la respuesta de generar código
 */
data class CodigoReferidoResponse(
    @SerializedName("codigo")
    val codigo: String = ""
)