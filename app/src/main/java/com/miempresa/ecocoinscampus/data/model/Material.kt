package com.miempresa.ecocoinscampus.data.model

import com.google.gson.annotations.SerializedName

data class Material(
    @SerializedName("_id")
    val id: String,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("cantidad")
    val cantidad: Double,

    @SerializedName("ecocoins_generadas")
    val ecocoinsGeneradas: Double,

    @SerializedName("usuario_id")
    val usuarioId: String,

    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("punto_recoleccion")
    val puntoRecoleccion: String? = null
)

data class RegisterMaterialRequest(
    val tipo: String,
    val cantidad: Double,
    val usuario_id: String,
    val punto_recoleccion: String
)

data class MaterialResponse(
    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("material")
    val material: Material
)