package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReciclajeRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun registrarReciclaje(
        usuarioId: String,
        material: String,
        pesoKg: Double,
        ubicacion: String,
        contenedorId: String
    ): Resource<Reciclaje> {
        return withContext(Dispatchers.IO) {
            try {
                val reciclajeData = mapOf<String, Any>(
                    "usuarioId" to usuarioId,
                    "material" to material,
                    "pesoKg" to pesoKg,
                    "ubicacion" to ubicacion,
                    "contenedorId" to contenedorId
                )

                val response = apiService.registrarReciclaje(reciclajeData)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al registrar reciclaje")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerReciclajesUsuario(usuarioId: String): Resource<List<Reciclaje>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerReciclajesUsuario(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener reciclajes")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun validarConIA(imageBase64: String): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val imageData = mapOf("imagen" to imageBase64)

                val response = apiService.validarConIA(imageData)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al validar imagen")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}