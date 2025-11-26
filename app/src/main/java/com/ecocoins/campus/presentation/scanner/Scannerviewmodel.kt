package com.ecocoins.campus.presentation.scanner

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Validating)
    val validationState: StateFlow<ValidationState> = _validationState.asStateFlow()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    // ⭐ IMPORTANTE: Reemplazar con tu API Key de Claude
    private val CLAUDE_API_KEY = "tu_claude_api_key_aqui"

    /**
     * Valida la foto del material con la API de Claude
     */
    fun validateWithAI(
        photoFile: File,
        material: TipoMaterial,
        qrCode: String
    ) {
        viewModelScope.launch {
            try {
                _validationState.value = ValidationState.Validating
                Log.d("ScannerVM", "🤖 Iniciando validación con IA...")
                Log.d("ScannerVM", "   Material esperado: ${material.nombre}")
                Log.d("ScannerVM", "   QR Code: $qrCode")

                // Convertir imagen a base64
                val imageBase64 = withContext(Dispatchers.IO) {
                    val bytes = photoFile.readBytes()
                    Base64.encodeToString(bytes, Base64.NO_WRAP)
                }

                // Llamar a Claude API
                val aiResponse = callClaudeAPI(imageBase64, material)

                Log.d("ScannerVM", "✅ Respuesta de IA recibida")
                Log.d("ScannerVM", "   Es válido: ${aiResponse.esValido}")
                Log.d("ScannerVM", "   Confianza: ${aiResponse.confianza}%")

                if (aiResponse.esValido) {
                    // Material validado correctamente
                    val ecoCoinsGanados = calcularEcoCoins(material, aiResponse.confianza)

                    _validationState.value = ValidationState.Success(
                        ecoCoinsGanados = ecoCoinsGanados,
                        mensaje = aiResponse.explicacion
                    )

                    // TODO: Registrar reciclaje en el backend
                    // Por ahora solo mostramos el éxito
                    Log.d("ScannerVM", "💾 Reciclaje validado: +$ecoCoinsGanados EcoCoins")
                } else {
                    // Material rechazado
                    _validationState.value = ValidationState.Rejected(
                        razon = aiResponse.explicacion,
                        materialDetectado = aiResponse.materialDetectado
                    )
                }
            } catch (e: Exception) {
                Log.e("ScannerVM", "❌ Error en validación con IA", e)
                _validationState.value = ValidationState.Error(
                    mensaje = "Error al validar con IA: ${e.message}"
                )
            }
        }
    }

    /**
     * Llama a la API de Claude para validar la imagen
     */
    private suspend fun callClaudeAPI(
        imageBase64: String,
        material: TipoMaterial
    ): AIValidationResponse = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Analiza esta imagen y determina si el material que se muestra corresponde a ${material.nombre}.
                
                Criterios de validación:
                ${material.ejemplos.joinToString("\n") { "- $it" }}
                
                Responde ÚNICAMENTE con un JSON en este formato exacto:
                {
                    "es_valido": true o false,
                    "confianza": número entre 0-100,
                    "material_detectado": "nombre del material que ves",
                    "explicacion": "breve explicación de tu decisión (máximo 2 líneas)"
                }
                
                IMPORTANTE: 
                - Si es claramente ${material.nombre}, es_valido = true
                - Si no estás seguro o es otro material, es_valido = false
                - Sé estricto pero justo en tu evaluación
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("model", "claude-3-5-sonnet-20241022")
                put("max_tokens", 300)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", JSONArray().apply {
                            put(JSONObject().apply {
                                put("type", "image")
                                put("source", JSONObject().apply {
                                    put("type", "base64")
                                    put("media_type", "image/jpeg")
                                    put("data", imageBase64)
                                })
                            })
                            put(JSONObject().apply {
                                put("type", "text")
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", CLAUDE_API_KEY)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("ScannerVM", "📡 Claude API Response Code: ${response.code}")
            Log.d("ScannerVM", "📡 Claude API Response: $responseBody")

            if (!response.isSuccessful) {
                throw Exception("Error en API de Claude: ${response.code} - $responseBody")
            }

            // Parsear respuesta de Claude
            val jsonResponse = JSONObject(responseBody ?: "{}")
            val content = jsonResponse.getJSONArray("content")
            val textContent = content.getJSONObject(0).getString("text")

            // Extraer el JSON de la respuesta de Claude
            val aiJson = extractJSON(textContent)

            AIValidationResponse(
                esValido = aiJson.getBoolean("es_valido"),
                confianza = aiJson.getInt("confianza"),
                materialDetectado = aiJson.optString("material_detectado", null),
                explicacion = aiJson.getString("explicacion")
            )
        } catch (e: Exception) {
            Log.e("ScannerVM", "❌ Error llamando a Claude API", e)
            throw e
        }
    }

    /**
     * Extrae el JSON de la respuesta de texto de Claude
     */
    private fun extractJSON(text: String): JSONObject {
        // Buscar el JSON dentro del texto (puede estar envuelto en markdown)
        val jsonStart = text.indexOf("{")
        val jsonEnd = text.lastIndexOf("}") + 1

        if (jsonStart == -1 || jsonEnd <= jsonStart) {
            throw Exception("No se encontró JSON válido en la respuesta")
        }

        val jsonString = text.substring(jsonStart, jsonEnd)
        return JSONObject(jsonString)
    }

    /**
     * Calcula los EcoCoins según el material y la confianza de la IA
     */
    private fun calcularEcoCoins(material: TipoMaterial, confianza: Int): Int {
        val baseCoins = material.ecoCoinsBase

        // Bonus por alta confianza
        val bonus = when {
            confianza >= 95 -> 1.2 // +20%
            confianza >= 85 -> 1.1 // +10%
            else -> 1.0 // Sin bonus
        }

        return (baseCoins * bonus).toInt()
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

data class AIValidationResponse(
    val esValido: Boolean,
    val confianza: Int,
    val materialDetectado: String?,
    val explicacion: String
)