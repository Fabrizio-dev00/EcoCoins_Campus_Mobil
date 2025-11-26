package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request para canjear una recompensa
 */
data class CanjeRequest(
    @SerializedName("usuarioId")
    val usuarioId: String,

    @SerializedName("recompensaId")
    val recompensaId: String
)