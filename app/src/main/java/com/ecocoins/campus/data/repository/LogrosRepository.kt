package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.data.model.LogrosResumen
import com.ecocoins.campus.data.model.LogrosResponse
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogrosRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getLogrosUsuario(usuarioId: String): Flow<Resource<List<Logro>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getLogrosUsuario(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                // ⭐ Ahora accedemos a .data.logros
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data.logros))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error al obtener logros"))
                }
            } else {
                emit(Resource.Error("Error al obtener logros: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getResumenLogros(usuarioId: String): Flow<Resource<LogrosResumen>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getResumenLogros(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                // ⭐ Ahora accedemos a .data
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error al obtener resumen"))
                }
            } else {
                emit(Resource.Error("Error al obtener resumen: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun verificarLogros(usuarioId: String): Flow<Resource<List<Logro>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.verificarLogros(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                // ⭐ Ahora accedemos a .data
                if (apiResponse.success && apiResponse.data != null) {
                    emit(Resource.Success(apiResponse.data))
                } else {
                    emit(Resource.Error(apiResponse.message ?: "Error al verificar logros"))
                }
            } else {
                emit(Resource.Error("Error al verificar logros: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}
