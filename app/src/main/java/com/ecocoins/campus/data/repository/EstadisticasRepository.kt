package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.EstadisticasDetalladas
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EstadisticasRepository {

    private val estadisticasService = RetrofitClient.estadisticasService

    suspend fun obtenerEstadisticasCompletas(usuarioId: String): Resource<EstadisticasDetalladas> {
        return withContext(Dispatchers.IO) {
            try {
                val response = estadisticasService.obtenerEstadisticasCompletas(usuarioId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener estadísticas")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}