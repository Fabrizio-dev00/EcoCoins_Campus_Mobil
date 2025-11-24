package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

data class Reciclaje(
    @SerializedName("_id")
    val id: String,

    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("tipoMaterial")
    val tipoMaterial: String,

    @SerializedName("pesoKg")
    val pesoKg: Double,

    @SerializedName("ecoCoinsGanadas")
    val ecoCoinsGanadas: Int,

    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("contenedorCodigo")
    val contenedorCodigo: String? = null,

    @SerializedName("puntoRecoleccion")
    val puntoRecoleccion: String? = null,

    @SerializedName("fotoUrl")
    val fotoUrl: String? = null,

    @SerializedName("verificado")
    val verificado: Boolean = false
)

data class ReciclajeRequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("tipoMaterial")
    val tipoMaterial: String,

    @SerializedName("pesoKg")
    val pesoKg: Double,

    @SerializedName("puntoRecoleccion")
    val puntoRecoleccion: String? = null
)