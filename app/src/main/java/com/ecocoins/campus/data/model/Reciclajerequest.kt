package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request para registrar un nuevo reciclaje
 */
data class ReciclajeRequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("tipoMaterial")
    val tipoMaterial: String, // "PLASTICO", "PAPEL", "VIDRIO", "METAL"

    @SerializedName("cantidad")
    val cantidad: Double,

    @SerializedName("peso")
    val peso: Double, // en kg

    @SerializedName("codigoQR")
    val codigoQR: String,

    @SerializedName("ecoCoinsGanados")
    val ecoCoinsGanados: Int,

    @SerializedName("validadoPorIA")
    val validadoPorIA: Boolean = true,

    @SerializedName("observaciones")
    val observaciones: String? = null
)