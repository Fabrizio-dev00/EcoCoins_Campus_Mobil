package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.utils.Result
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecompensasRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun obtenerRecompensas(): Result<List<Recompensa>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerRecompensas()

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error al obtener recompensas")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    suspend fun obtenerRecompensa(recompensaId: String): Result<Recompensa> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerRecompensa(recompensaId)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error al obtener recompensa")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    suspend fun canjearRecompensa(
        usuarioId: String,
        recompensaId: String
    ): Result<Canje> {
        return withContext(Dispatchers.IO) {
            try {
                val canjeData = mapOf(
                    "usuarioId" to usuarioId,
                    "recompensaId" to recompensaId
                )

                val response = apiService.canjearRecompensa(canjeData)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error al canjear recompensa")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    suspend fun obtenerCanjesUsuario(
        usuarioId: String,
        estado: String? = null
    ): Result<List<Canje>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerCanjesUsuario(usuarioId, estado)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error al obtener canjes")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    suspend fun obtenerCanje(canjeId: String): Result<Canje> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerCanje(canjeId)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error al obtener canje")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }
}