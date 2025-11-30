package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapaRepository {

    private val mapaService = RetrofitClient.mapaService

    suspend fun obtenerPuntos(
        tipo: String? = null,
        estado: String? = null
    ): Resource<List<PuntoReciclaje>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = mapaService.obtenerPuntos(tipo, estado)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener puntos")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerPunto(puntoId: String): Resource<PuntoReciclaje> {
        return withContext(Dispatchers.IO) {
            try {
                val response = mapaService.obtenerPunto(puntoId)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener punto")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun obtenerPuntosCercanos(
        latitud: Double,
        longitud: Double,
        radioKm: Double = 5.0
    ): Resource<List<PuntoReciclaje>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = mapaService.obtenerPuntosCercanos(latitud, longitud, radioKm)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al obtener puntos cercanos")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun filtrarPorMaterial(material: String): Resource<List<PuntoReciclaje>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = mapaService.filtrarPorMaterial(material)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error al filtrar puntos")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}