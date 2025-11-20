package com.miempresa.ecocoinscampus.data.repository

import com.miempresa.ecocoinscampus.data.local.UserPreferences
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.remote.ApiService
import com.miempresa.ecocoinscampus.utils.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReciclajeRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Obtener reciclajes del usuario actual
     */
    suspend fun getUserReciclajes(): Result<List<Reciclaje>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getReciclajesByUsuario(userId, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener reciclajes")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Registrar reciclaje manual
     */
    suspend fun registrarReciclaje(
        tipoMaterial: String,
        pesoKg: Double,
        puntoRecoleccion: String
    ): Result<Reciclaje> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val request = ReciclajeRequest(
                usuarioId = userId,
                tipoMaterial = tipoMaterial,
                pesoKg = pesoKg,
                puntoRecoleccion = puntoRecoleccion
            )

            val response = apiService.registrarReciclaje("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val reciclaje = apiResponse.data

                    // Actualizar EcoCoins localmente
                    val currentCoins = userPreferences.ecoCoins.first()
                    userPreferences.updateEcoCoins(currentCoins + reciclaje.ecoCoinsGanadas)

                    Result.Success(reciclaje)
                } else {
                    Result.Error(apiResponse.message ?: "Error al registrar reciclaje")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Registrar reciclaje con QR
     */
    suspend fun registrarConQr(
        contenedorCodigo: String,
        pesoKg: Double,
        fotoUrl: String? = null,
        observaciones: String? = null
    ): Result<ReciclajeQrResponse> {
        return try {
            val token = userPreferences.authToken.first() ?: ""
            val userId = userPreferences.userId.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val request = ReciclajeQrRequest(
                usuarioId = userId,
                contenedorCodigo = contenedorCodigo,
                pesoKg = pesoKg,
                fotoUrl = fotoUrl,
                observaciones = observaciones
            )

            val response = apiService.registrarConQr("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val qrResponse = apiResponse.data

                    // Actualizar EcoCoins localmente
                    userPreferences.updateEcoCoins(qrResponse.nuevoBalance.toDouble())

                    Result.Success(qrResponse)
                } else {
                    Result.Error(apiResponse.message ?: "Error al registrar reciclaje")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Validar código QR
     */
    suspend fun validarQr(codigo: String): Result<ContenedorInfo> {
        return try {
            val token = userPreferences.authToken.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.validarQr(codigo, "Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Código QR inválido")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Obtener tarifas de materiales
     */
    suspend fun getTarifas(): Result<Map<String, Int>> {
        return try {
            val token = userPreferences.authToken.first() ?: ""

            if (token.isEmpty()) {
                return Result.Error("No hay sesión activa")
            }

            val response = apiService.getTarifas("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    Result.Error(apiResponse.message ?: "Error al obtener tarifas")
                }
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.localizedMessage}", e)
        }
    }

    /**
     * Tipos de materiales disponibles
     */
    fun getTiposMateriales(): List<String> = listOf(
        "Plástico",
        "Papel",
        "Vidrio",
        "Metal",
        "Cartón",
        "Electrónico",
        "Orgánico",
        "Pilas"
    )
}