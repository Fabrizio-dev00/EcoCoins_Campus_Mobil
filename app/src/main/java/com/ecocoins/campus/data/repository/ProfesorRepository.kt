package com.ecocoins.campus.data.repository

import android.util.Log
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.CanjearRecompensaProfesorRequest
import com.ecocoins.campus.data.model.CanjearRecompensaProfesorResponse
import com.ecocoins.campus.data.model.Profesor
import com.ecocoins.campus.data.remote.ApiService
import com.ecocoins.campus.utils.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfesorRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    /**
     * Obtiene el token de autenticación de Firebase
     */
    private suspend fun getAuthToken(): String? = withContext(Dispatchers.IO) {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val token = currentUser.getIdToken(false).await().token
                "Bearer $token"
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ProfesorRepository", "❌ Error obteniendo token: ${e.message}")
            null
        }
    }

    /**
     * Obtiene el ID del usuario desde UserPreferences
     */
    private suspend fun getUserId(): String? {
        return userPreferences.userId.firstOrNull()
    }

    /**
     * Obtener todos los profesores activos con sus recompensas
     */
    suspend fun getProfesoresActivos(): Result<List<Profesor>> = withContext(Dispatchers.IO) {
        try {
            Log.d("ProfesorRepository", "📚 Obteniendo profesores activos...")

            val token = getAuthToken()
            if (token == null) {
                Log.e("ProfesorRepository", "❌ No hay token de autenticación")
                return@withContext Result.Error("Sesión expirada. Por favor inicia sesión nuevamente.")
            }

            val response = apiService.getProfesoresActivos(token)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success) {
                    val profesores = apiResponse.data ?: emptyList()
                    Log.d("ProfesorRepository", "✅ Profesores obtenidos: ${profesores.size}")
                    Result.Success(profesores)
                } else {
                    val errorMsg = apiResponse.message ?: "Error al obtener profesores"
                    Log.e("ProfesorRepository", "❌ $errorMsg")
                    Result.Error(errorMsg)
                }
            } else {
                val errorMsg = "Error al obtener profesores: ${response.code()}"
                Log.e("ProfesorRepository", "❌ $errorMsg")
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("ProfesorRepository", "❌ Excepción al obtener profesores", e)
            Result.Error(e.message ?: "Error desconocido al cargar profesores")
        }
    }

    /**
     * Obtener un profesor específico por ID
     */
    suspend fun getProfesorById(profesorId: String): Result<Profesor> = withContext(Dispatchers.IO) {
        try {
            Log.d("ProfesorRepository", "📚 Obteniendo profesor con ID: $profesorId")

            val token = getAuthToken()
            if (token == null) {
                return@withContext Result.Error("Sesión expirada. Por favor inicia sesión nuevamente.")
            }

            val response = apiService.getProfesorById(profesorId, token)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val profesor = apiResponse.data
                    Log.d("ProfesorRepository", "✅ Profesor obtenido: ${profesor.getNombreCompleto()}")
                    Result.Success(profesor)
                } else {
                    val errorMsg = apiResponse.message ?: "Error al obtener profesor"
                    Log.e("ProfesorRepository", "❌ $errorMsg")
                    Result.Error(errorMsg)
                }
            } else {
                val errorMsg = "Error al obtener profesor: ${response.code()}"
                Log.e("ProfesorRepository", "❌ $errorMsg")
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("ProfesorRepository", "❌ Excepción al obtener profesor", e)
            Result.Error(e.message ?: "Error desconocido al cargar profesor")
        }
    }

    /**
     * Canjear una recompensa de un profesor
     */
    suspend fun canjearRecompensa(
        profesorId: String,
        recompensaId: String
    ): Result<CanjearRecompensaProfesorResponse> = withContext(Dispatchers.IO) {
        try {
            // Obtener el ID del usuario desde UserPreferences (Flow)
            val usuarioId = getUserId()

            if (usuarioId.isNullOrEmpty()) {
                Log.e("ProfesorRepository", "❌ No hay usuario logueado")
                return@withContext Result.Error("Debes iniciar sesión para canjear recompensas")
            }

            val token = getAuthToken()
            if (token == null) {
                return@withContext Result.Error("Sesión expirada. Por favor inicia sesión nuevamente.")
            }

            Log.d("ProfesorRepository", "💰 Canjeando recompensa...")
            Log.d("ProfesorRepository", "   Usuario ID: $usuarioId")
            Log.d("ProfesorRepository", "   Profesor ID: $profesorId")
            Log.d("ProfesorRepository", "   Recompensa ID: $recompensaId")

            val request = CanjearRecompensaProfesorRequest(
                usuarioId = usuarioId,
                profesorId = profesorId,
                recompensaId = recompensaId
            )

            val response = apiService.canjearRecompensaProfesor(token, request)

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!

                if (apiResponse.success && apiResponse.data != null) {
                    val canjeResponse = apiResponse.data
                    Log.d("ProfesorRepository", "✅ Recompensa canjeada exitosamente")
                    Log.d("ProfesorRepository", "   Nuevo balance: ${canjeResponse.nuevoBalance} EcoCoins")
                    Result.Success(canjeResponse)
                } else {
                    val errorMsg = apiResponse.message ?: "Error al canjear recompensa"
                    Log.e("ProfesorRepository", "❌ $errorMsg")
                    Result.Error(errorMsg)
                }
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "No tienes suficientes EcoCoins"
                    404 -> "Recompensa no encontrada"
                    409 -> "Recompensa no disponible"
                    else -> "Error al canjear recompensa: ${response.code()}"
                }
                Log.e("ProfesorRepository", "❌ $errorMsg")
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("ProfesorRepository", "❌ Excepción al canjear recompensa", e)
            Result.Error(e.message ?: "Error desconocido al canjear recompensa")
        }
    }

    /**
     * Obtener historial de canjes del usuario con profesores
     */
    suspend fun getHistorialCanjesProfesores(): Result<List<CanjearRecompensaProfesorResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val usuarioId = getUserId()

                if (usuarioId.isNullOrEmpty()) {
                    return@withContext Result.Error("Debes iniciar sesión")
                }

                val token = getAuthToken()
                if (token == null) {
                    return@withContext Result.Error("Sesión expirada. Por favor inicia sesión nuevamente.")
                }

                Log.d("ProfesorRepository", "📜 Obteniendo historial de canjes...")

                val response = apiService.getHistorialCanjesProfesores(usuarioId, token)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.success) {
                        val historial = apiResponse.data ?: emptyList()
                        Log.d("ProfesorRepository", "✅ Historial obtenido: ${historial.size} canjes")
                        Result.Success(historial)
                    } else {
                        val errorMsg = apiResponse.message ?: "Error al obtener historial"
                        Log.e("ProfesorRepository", "❌ $errorMsg")
                        Result.Error(errorMsg)
                    }
                } else {
                    val errorMsg = "Error al obtener historial: ${response.code()}"
                    Log.e("ProfesorRepository", "❌ $errorMsg")
                    Result.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("ProfesorRepository", "❌ Excepción al obtener historial", e)
                Result.Error(e.message ?: "Error desconocido al cargar historial")
            }
        }
}