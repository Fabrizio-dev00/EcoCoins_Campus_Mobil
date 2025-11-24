package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

data class Recompensa(
    @SerializedName("_id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("costoEcocoins")
    val costoEcocoins: Int,

    @SerializedName("stock")
    val stock: Int = 0,

    @SerializedName("categoria")
    val categoria: String? = null,

    @SerializedName("imagenUrl")
    val imagenUrl: String? = null,

    @SerializedName("disponible")
    val disponible: Boolean = true
)

data class CanjeRequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("recompensaId")
    val recompensaId: String,

    @SerializedName("direccionEntrega")
    val direccionEntrega: String? = null,

    @SerializedName("telefonoContacto")
    val telefonoContacto: String? = null,

    @SerializedName("observaciones")
    val observaciones: String? = null
)

data class CanjeResponse(
    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("canjeId")
    val canjeId: String,

    @SerializedName("recompensa")
    val recompensa: String,

    @SerializedName("costoEcoCoins")
    val costoEcoCoins: Int,

    @SerializedName("nuevoBalance")
    val nuevoBalance: Int,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("fechaCanje")
    val fechaCanje: String
)

data class Canje(
    @SerializedName("_id")
    val id: String,

    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("usuarioNombre")
    val usuarioNombre: String,

    @SerializedName("recompensaId")
    val recompensaId: String,

    @SerializedName("recompensaNombre")
    val recompensaNombre: String,

    @SerializedName("costoEcoCoins")
    val costoEcoCoins: Int,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("fechaCanje")
    val fechaCanje: String,

    @SerializedName("fechaEntrega")
    val fechaEntrega: String? = null,

    @SerializedName("direccionEntrega")
    val direccionEntrega: String? = null
)