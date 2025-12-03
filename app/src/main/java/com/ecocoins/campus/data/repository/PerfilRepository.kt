package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PerfilRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getPerfil(usuarioId: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.getPerfil(usuarioId)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al obtener perfil: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun updatePerfil(usuarioId: String, user: User): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.updatePerfil(usuarioId, user)

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al actualizar perfil: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }
}