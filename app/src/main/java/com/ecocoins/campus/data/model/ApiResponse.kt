package com.ecocoins.campus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Clase genérica para manejar respuestas del backend
 * Coincide con ApiResponse.java del backend
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: T?,

    @SerializedName("timestamp")
    val timestamp: String?,

    @SerializedName("error")
    val error: String? = null
)

/**
 * Clase sellada para manejar estados de operaciones
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}