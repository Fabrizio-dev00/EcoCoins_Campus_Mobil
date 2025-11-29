package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request para validar material con IA
 */
data class ValidarIARequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("tipoMaterial")
    val tipoMaterial: String,

    @SerializedName("codigoQR")
    val codigoQR: String,

    @SerializedName("imagenBase64")
    val imagenBase64: String
)

/**
 * Response de validación con IA
 */
data class ValidarIAResponse(
    @SerializedName("validado")
    val validado: Boolean,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("ecoCoinsGanados")
    val ecoCoinsGanados: Int = 0,

    @SerializedName("razon")
    val razon: String? = null,

    @SerializedName("materialDetectado")
    val materialDetectado: String? = null,

    @SerializedName("confianza")
    val confianza: Int = 0,

    @SerializedName("reciclaje")
    val reciclaje: Reciclaje? = null
)