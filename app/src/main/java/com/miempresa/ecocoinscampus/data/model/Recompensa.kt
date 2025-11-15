package com.miempresa.ecocoinscampus.data.model

import com.google.gson.annotations.SerializedName

data class Recompensa(
    @SerializedName("_id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("costo_ecocoins")
    val costoEcocoins: Double,

    @SerializedName("imagen")
    val imagen: String? = null,

    @SerializedName("disponible")
    val disponible: Boolean = true,

    @SerializedName("categoria")
    val categoria: String? = null
)

data class CanjearRecompensaRequest(
    val usuario_id: String,
    val recompensa_id: String
)