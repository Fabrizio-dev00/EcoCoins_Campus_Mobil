package com.ecocoins.campus.presentation.logros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.LogrosRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogrosViewModel @Inject constructor(
    private val logrosRepository: LogrosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // ✅ CORREGIDO: Usar StateFlow en lugar de LiveData
    private val _uiState = MutableStateFlow(LogrosUiState())
    val uiState: StateFlow<LogrosUiState> = _uiState.asStateFlow()

    init {
        loadLogros()
    }

    // ✅ CORREGIDO: Renombrado de cargarLogros() a loadLogros()
    fun loadLogros() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // ✅ CORREGIDO: getUserId() ahora es suspending
            val usuarioId = userPreferences.getUserId()
            if (usuarioId.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Usuario no identificado"
                    )
                }
                return@launch
            }

            when (val result = logrosRepository.obtenerLogrosUsuario(usuarioId)) {
                is Resource.Success -> {
                    val data = result.data

                    if (data != null) {
                        try {
                            // Extraer logros
                            val logrosData = data["logros"] as? List<*>
                            val gson = Gson()
                            val logrosJson = gson.toJson(logrosData)
                            val logrosType = object : TypeToken<List<Logro>>() {}.type
                            val logrosList: List<Logro> = gson.fromJson(logrosJson, logrosType)

                            // Extraer totales
                            val totalLogros = (data["totalLogros"] as? Number)?.toInt() ?: logrosList.size
                            val logrosDesbloqueados = (data["logrosDesbloqueados"] as? Number)?.toInt()
                                ?: logrosList.count { it.desbloqueado }
                            val porcentaje = (data["porcentajeCompletado"] as? Number)?.toInt()
                                ?: if (totalLogros > 0) (logrosDesbloqueados * 100 / totalLogros) else 0

                            _uiState.update {
                                it.copy(
                                    logros = logrosList,
                                    totalLogros = totalLogros,
                                    totalDesbloqueados = logrosDesbloqueados,
                                    porcentajeCompletado = porcentaje,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        } catch (e: Exception) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Error al procesar logros: ${e.message}"
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "No se recibieron datos de logros"
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al cargar logros"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Ya manejado con isLoading
                }
            }
        }
    }

    fun verificarLogros() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = logrosRepository.verificarLogros(usuarioId)) {
                is Resource.Success -> {
                    val data = result.data
                    val nuevosLogros = data?.get("nuevosLogrosDesbloqueados") as? List<*>

                    if (!nuevosLogros.isNullOrEmpty()) {
                        // Recargar logros para reflejar cambios
                        loadLogros()
                    }
                }
                is Resource.Error -> {
                    // Error silencioso, no molestamos al usuario
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadLogros()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

// ✅ AGREGADO: Clase UiState que faltaba
data class LogrosUiState(
    val logros: List<Logro> = emptyList(),
    val totalLogros: Int = 0,
    val totalDesbloqueados: Int = 0,
    val porcentajeCompletado: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)