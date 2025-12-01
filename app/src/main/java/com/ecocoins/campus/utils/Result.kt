package com.ecocoins.campus.utils

/**
 * Clase sellada para representar el estado de una operación asíncrona.
 *
 * @param T El tipo de datos que se espera en caso de éxito
 */
sealed class Result<out T> {
    /**
     * Estado de éxito - la operación se completó correctamente
     * @param data Los datos resultantes de la operación
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Estado de error - la operación falló
     * @param message Mensaje descriptivo del error
     * @param exception Excepción opcional que causó el error
     */
    data class Error(
        val message: String,
        val exception: Exception? = null
    ) : Result<Nothing>()

    /**
     * Estado de carga - la operación está en progreso
     * IMPORTANTE: Usa Nothing para evitar problemas de tipos
     */
    object Loading : Result<Nothing>()  // ✅ CLAVE: object + Nothing
}

// Extension functions para manejo funcional
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (String) -> Unit): Result<T> {
    if (this is Result.Error) action(message)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

// Helper para acceder a data de forma segura
val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

// Helper para acceder a message de forma segura
val <T> Result<T>.message: String?
    get() = (this as? Result.Error)?.message