package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.LoginRequest
import com.ecocoins.campus.data.model.RegisterRequest
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    fun login(
        request: LoginRequest
    ): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!! // ApiResponse<User>
                val user = apiResponse.data // Acceder a 'data' en lugar de 'usuario'

                if (user != null) {
                    userPreferences.saveUserData(
                        userId = user.id,
                        name = user.nombre,
                        email = user.correo,
                        token = "", // No hay token en login
                        ecoCoins = user.ecoCoins
                    )

                    emit(Resource.Success(user))
                } else {
                    emit(Resource.Error("No se recibieron datos del usuario"))
                }
            } else {
                emit(Resource.Error("Error al iniciar sesión: ${response.message()}"))
            }

        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    fun register(
        request: RegisterRequest
    ): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!! // ApiResponse<User>
                val user = apiResponse.data // Acceder a 'data' en lugar de 'usuario'

                if (user != null) {
                    userPreferences.saveUserData(
                        userId = user.id,
                        name = user.nombre,
                        email = user.correo,
                        token = "", // No hay token en registro
                        ecoCoins = user.ecoCoins
                    )

                    emit(Resource.Success(user))
                } else {
                    emit(Resource.Error("No se recibieron datos del usuario"))
                }
            } else {
                emit(Resource.Error("Error al registrarse: ${response.message()}"))
            }

        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    suspend fun logout() {
        userPreferences.clearUserData()
    }

    fun getUserId(): Flow<String?> = userPreferences.userId

    fun isLoggedIn(): Flow<Boolean> = userPreferences.isLoggedIn

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

    suspend fun updatePerfil(usuarioId: Long, user: User): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())

            val response = apiService.updatePerfil(usuarioId.toString(), user)

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
