package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.PuntoReciclaje
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MapaRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getPuntosReciclaje(): Flow<Resource<List<PuntoReciclaje>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getPuntosReciclaje()

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener puntos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getPuntosCercanos(
        latitud: Double,
        longitud: Double,
        radio: Double = 5.0
    ): Flow<Resource<List<PuntoReciclaje>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getPuntosCercanos(latitud, longitud, radio)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener puntos cercanos: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getPuntoById(puntoId: Long): Flow<Resource<PuntoReciclaje>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getPuntoById(puntoId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener punto: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}