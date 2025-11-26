package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

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