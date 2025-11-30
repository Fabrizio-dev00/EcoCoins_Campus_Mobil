package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogrosRepository {

    private val logrosService = RetrofitClient.logrosService

    suspend fun obtenerTodosLosLogros(): Resource<List<Logro>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = logrosService.obtenerTodosLosLogros()

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener logros")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerLogrosUsuario(usuarioId: String): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = logrosService.obtenerLogrosUsuario(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener logros del usuario")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun verificarLogros(usuarioId: String): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = logrosService.verificarLogros(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al verificar logros")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}