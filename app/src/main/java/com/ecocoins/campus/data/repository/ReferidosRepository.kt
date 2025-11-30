package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.CodigoReferidoResponse
import com.ecocoins.campus.data.model.ReferidosInfo
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReferidosRepository {

    private val referidosService = RetrofitClient.referidosService

    suspend fun obtenerReferidos(usuarioId: String): Resource<ReferidosInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = referidosService.obtenerReferidos(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener referidos")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun generarCodigo(
        usuarioId: String,
        nombre: String
    ): Resource<CodigoReferidoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val userData = mapOf(
                    "usuarioId" to usuarioId,
                    "nombre" to nombre
                )

                val response = referidosService.generarCodigo(userData)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al generar código")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun registrarReferido(
        codigo: String,
        nuevoUsuarioId: String
    ): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val referidoData = mapOf(
                    "codigo" to codigo,
                    "nuevoUsuarioId" to nuevoUsuarioId
                )

                val response = referidosService.registrarReferido(referidoData)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al registrar referido")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun validarCodigo(codigo: String): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = referidosService.validarCodigo(codigo)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Código inválido")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}