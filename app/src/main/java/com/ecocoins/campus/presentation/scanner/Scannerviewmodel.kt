package com.ecocoins.campus.presentation.scanner

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.ValidarIARequest
import com.ecocoins.campus.data.remote.ApiService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Validating)
    val validationState: StateFlow<ValidationState> = _validationState.asStateFlow()

    /**
     * Valida la foto del material con el backend (Gemini AI)
     */
    fun validateWithAI(
        photoFile: File,
        material: TipoMaterial,
        qrCode: String
    ) {
        viewModelScope.launch {
            try {
                _validationState.value = ValidationState.Validating
                Log.d("ScannerVM", "🤖 Iniciando validación con backend Gemini...")
                Log.d("ScannerVM", "   Material esperado: ${material.nombre}")
                Log.d("ScannerVM", "   QR Code: $qrCode")

                // Obtener usuario ID
                val userId = getCurrentUserId()
                if (userId == null) {
                    Log.e("ScannerVM", "❌ Usuario no autenticado")
                    _validationState.value = ValidationState.Error(
                        mensaje = "Debes iniciar sesión para validar materiales"
                    )
                    return@launch
                }

                // Obtener token de Firebase
                val token = getAuthToken()
                if (token == null) {
                    Log.e("ScannerVM", "❌ No se pudo obtener token")
                    _validationState.value = ValidationState.Error(
                        mensaje = "Error de autenticación. Intenta iniciar sesión nuevamente"
                    )
                    return@launch
                }

                // Convertir imagen a base64
                val imageBase64 = withContext(Dispatchers.IO) {
                    val bytes = photoFile.readBytes()
                    Base64.encodeToString(bytes, Base64.NO_WRAP)
                }

                Log.d("ScannerVM", "📸 Imagen convertida a base64 (${imageBase64.length} caracteres)")

                // Crear request
                val request = ValidarIARequest(
                    usuarioId = userId,
                    tipoMaterial = material.nombre.uppercase(),
                    codigoQR = qrCode,
                    imagenBase64 = imageBase64
                )

                // Llamar al backend
                Log.d("ScannerVM", "📡 Llamando al backend...")
                val response = apiService.validarMaterialConIA("Bearer $token", request)

                Log.d("ScannerVM", "📡 Response Code: ${response.code()}")
                Log.d("ScannerVM", "📡 Response Body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.success && apiResponse.data != null) {
                        val data = apiResponse.data

                        if (data.validado) {
                            // Material validado correctamente
                            Log.d("ScannerVM", "✅ Material validado: +${data.ecoCoinsGanados} EcoCoins")

                            _validationState.value = ValidationState.Success(
                                ecoCoinsGanados = data.ecoCoinsGanados,
                                mensaje = data.mensaje
                            )

                            // Actualizar EcoCoins localmente
                            val currentCoins = userPreferences.ecoCoins.firstOrNull() ?: 0.0
                            userPreferences.updateEcoCoins(currentCoins + data.ecoCoinsGanados)

                        } else {
                            // Material rechazado
                            Log.d("ScannerVM", "❌ Material rechazado: ${data.razon}")

                            _validationState.value = ValidationState.Rejected(
                                razon = data.razon ?: "El material no coincide con el tipo seleccionado",
                                materialDetectado = data.materialDetectado
                            )
                        }
                    } else {
                        throw Exception(apiResponse.message ?: "Error en la respuesta del servidor")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ScannerVM", "❌ Error HTTP ${response.code()}: $errorBody")
                    throw Exception("Error del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("ScannerVM", "❌ Error en validación con IA", e)
                _validationState.value = ValidationState.Error(
                    mensaje = "Error al validar: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Obtiene el token de autenticación de Firebase
     */
    private suspend fun getAuthToken(): String? = withContext(Dispatchers.IO) {
        try {
            FirebaseAuth.getInstance().currentUser
                ?.getIdToken(false)
                ?.await()
                ?.token
        } catch (e: Exception) {
            Log.e("ScannerVM", "❌ Error obteniendo token", e)
            null
        }
    }

    /**
     * Obtiene el UID del usuario actual
     */
    private suspend fun getCurrentUserId(): String? {
        return userPreferences.userId.firstOrNull()
            ?: FirebaseAuth.getInstance().currentUser?.uid
    }

    /**
     * Resetea el estado para un nuevo escaneo
     */
    fun resetScanner() {
        _uiState.value = ScannerUiState()
        _validationState.value = ValidationState.Validating
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ScannerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val reciclajeRegistrado: Boolean = false
)