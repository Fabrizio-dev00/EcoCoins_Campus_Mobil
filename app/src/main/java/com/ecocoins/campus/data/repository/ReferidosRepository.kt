package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.CodigoReferidoResponse
import com.ecocoins.campus.data.model.ReferidosInfo
import com.ecocoins.campus.data.remote.RetrofitClient
import com.ecocoins.campus.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReferidosRepository {

    private val apiService = RetrofitClient.referidosService

    suspend fun obtenerReferidos(usuarioId: String): Result<ReferidosInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerReferidos(usuarioId)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error al obtener referidos")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    suspend fun generarCodigo(usuarioId: String, nombre: String): Result<CodigoReferidoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val data = mapOf(
                    "usuarioId" to usuarioId,
                    "nombre" to nombre
                )

                // ✅ CORREGIDO: generarCodigo (no generarCodigoReferido)
                val response = apiService.generarCodigo(data)

                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.message ?: "Error al generar código")
                }
            } catch (e: Exception) {
                Result.Error(
                    message = e.message ?: "Error de conexión",
                    exception = e
                )
            }
        }
    }

    suspend fun validarCodigo(codigo: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // ✅ CORREGIDO: validarCodigo (no validarCodigoReferido)
                val response = apiService.validarCodigo(codigo)

                if (response.success) {
                    Result.Success(true)
                } else {
                    Result.Error(response.message ?: "Código inválido")
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