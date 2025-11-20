package com.miempresa.ecocoinscampus.data.model

import com.google.gson.annotations.SerializedName

data class Contenedor(
    @SerializedName("_id")
    val id: String,

    @SerializedName("codigo")
    val codigo: String,

    @SerializedName("tipoMaterial")
    val tipoMaterial: String,

    @SerializedName("ubicacion")
    val ubicacion: String,

    @SerializedName("capacidadMaxKg")
    val capacidadMaxKg: Double,

    @SerializedName("capacidadActualKg")
    val capacidadActualKg: Double,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("porcentajeLlenado")
    val porcentajeLlenado: Double
)

data class ContenedorInfo(
    @SerializedName("contenedorId")
    val contenedorId: String,

    @SerializedName("codigo")
    val codigo: String,

    @SerializedName("tipoMaterial")
    val tipoMaterial: String,

    @SerializedName("ubicacion")
    val ubicacion: String,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("capacidadDisponible")
    val capacidadDisponible: Double,

    @SerializedName("porcentajeLlenado")
    val porcentajeLlenado: Double,

    @SerializedName("tarifaPorKg")
    val tarifaPorKg: Int
)