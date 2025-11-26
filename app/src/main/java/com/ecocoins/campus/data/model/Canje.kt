package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de un canje realizado
 */
data class Canje(
    @SerializedName("_id")
    val id: String = "",

    @SerializedName("usuarioId")
    val usuarioId: String = "",

    @SerializedName("recompensaId")
    val recompensaId: String = "",

    @SerializedName("recompensaNombre")
    val recompensaNombre: String = "",

    @SerializedName("costoEcoCoins")
    val costoEcoCoins: Int = 0,

    @SerializedName("estado")
    val estado: String = "PENDIENTE", // "PENDIENTE", "COMPLETADO", "CANCELADO"

    @SerializedName("fechaCanje")
    val fechaCanje: String = "",

    @SerializedName("fechaEntrega")
    val fechaEntrega: String? = null
)

/**
 * Request para canjear una recompensa
 */
data class CanjeRequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("recompensaId")
    val recompensaId: String
)

/**
 * Response al canjear una recompensa
 */
data class CanjeResponse(
    @SerializedName("_id")
    val id: String,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("nuevoBalance")
    val nuevoBalance: Int,

    @SerializedName("recompensa")
    val recompensa: Recompensa,

    @SerializedName("fechaCanje")
    val fechaCanje: String
)