package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.model.ReciclajeRequest
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReciclajeRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun registrarReciclaje(
        materialTipo: String,
        cantidad: Double,
        peso: Double,
        ubicacion: String?,
        codigoQR: String?
    ): Flow<Resource<Reciclaje>> = flow {
        try {
            emit(Resource.Loading())

            val userId = userPreferences.userId

            // Obtener el userId del flujo
            userId.collect { id ->
                if (id != null) {
                    val request = ReciclajeRequest(
                        usuarioId = id,
                        materialTipo = materialTipo,
                        cantidad = cantidad,
                        peso = peso,
                        ubicacion = ubicacion,
                        codigoQR = codigoQR
                    )

                    val response = apiService.registrarReciclaje(request)

                    if (response.isSuccessful && response.body() != null) {
                        emit(Resource.Success(response.body()!!))
                    } else {
                        emit(Resource.Error("Error al registrar reciclaje: ${response.message()}"))
                    }
                } else {
                    emit(Resource.Error("Usuario no autenticado"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getReciclajesUsuario(usuarioId: String): Flow<Resource<List<Reciclaje>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getReciclajesUsuario(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener reciclajes: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getHistorialReciclajes(
        usuarioId: String,
        page: Int = 0,
        size: Int = 20
    ): Flow<Resource<List<Reciclaje>>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getHistorialReciclajes(usuarioId, page, size)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener historial: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun getReciclajeById(reciclajeId: Long): Flow<Resource<Reciclaje>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getReciclajeById(reciclajeId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener reciclaje: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}