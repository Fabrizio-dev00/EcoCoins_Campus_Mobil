package com.ecocoins.campus.data.repository

import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun login(email: String, password: String): Resource<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val credentials = mapOf(
                    "email" to email,
                    "password" to password
                )

                val response = apiService.login(credentials)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error en el login")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }

    suspend fun register(
        nombre: String,
        email: String,
        password: String
    ): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val userData = mapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "password" to password
                )

                val response = apiService.register(userData)

                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Error en el registro")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Error de conexión")
            }
        }
    }
}
