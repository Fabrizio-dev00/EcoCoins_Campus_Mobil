package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EstadisticasRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getEstadisticasUsuario(usuarioId: String): Flow<Resource<EstadisticasDetalladas>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getEstadisticasUsuario(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener estadísticas: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getResumenEstadisticas(usuarioId: String): Flow<Resource<Map<String, Any>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getResumenEstadisticas(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener resumen: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getDistribucionMateriales(usuarioId: Long): Flow<Resource<List<MaterialStats>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getDistribucionMateriales(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener distribución: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getTendenciaSemanal(usuarioId: Long): Flow<Resource<List<TendenciaDia>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getTendenciaSemanal(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener tendencia: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getImpactoAmbiental(usuarioId: Long): Flow<Resource<ImpactoAmbiental>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getImpactoAmbiental(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener impacto: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}
