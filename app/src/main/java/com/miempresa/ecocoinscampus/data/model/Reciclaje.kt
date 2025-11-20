package com.miempresa.ecocoinscampus.data.model

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

// REQUEST
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

// REQUEST PARA QR
data class ReciclajeQrRequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("contenedorCodigo")
    val contenedorCodigo: String,

    @SerializedName("pesoKg")
    val pesoKg: Double,

    @SerializedName("fotoUrl")
    val fotoUrl: String? = null,

    @SerializedName("observaciones")
    val observaciones: String? = null
)

// RESPONSE QR
data class ReciclajeQrResponse(
    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("reciclajeId")
    val reciclajeId: String,

    @SerializedName("ecoCoinsGanadas")
    val ecoCoinsGanadas: Int,

    @SerializedName("nuevoBalance")
    val nuevoBalance: Int,

    @SerializedName("tipoMaterial")
    val tipoMaterial: String,

    @SerializedName("ubicacion")
    val ubicacion: String,

    @SerializedName("totalReciclajes")
    val totalReciclajes: Int,

    @SerializedName("nivel")
    val nivel: String
)