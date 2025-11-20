package com.miempresa.ecocoinscampus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper genérico para todas las respuestas de la API
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null
)