package com.ecocoins.campus.presentation.reciclajes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.utils.Result
import com.ecocoins.campus.data.repository.ReciclajeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReciclajesViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReciclajesUiState())
    val uiState: StateFlow<ReciclajesUiState> = _uiState.asStateFlow()

    init {
        cargarReciclajes()
        cargarTiposMateriales()
    }

    private fun cargarReciclajes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val usuarioId = userPreferences.getUserId()
            if (usuarioId.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Usuario no autenticado"
                    )
                }
                return@launch
            }

            // ✅ CORREGIDO: When exhaustivo
            when (val result = reciclajeRepository.obtenerReciclajesUsuario(usuarioId)) {
                is Result.Success<*> -> {
                    _uiState.update {
                        it.copy(
                            reciclajes = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Ya está en loading
                }
                else -> {
                    // Caso por defecto (nunca debería llegar aquí)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Estado desconocido"
                        )
                    }
                }
            }
        }
    }

    private fun cargarTiposMateriales() {
        // Lista de tipos de materiales disponibles
        _uiState.update {
            it.copy(
                tiposMateriales = listOf(
                    "Plástico",
                    "Papel",
                    "Cartón",
                    "Vidrio",
                    "Metal",
                    "Aluminio",
                    "Electrónicos",
                    "Orgánicos"
                )
            )
        }
    }

    fun registrarReciclaje(
        material: String,
        pesoKg: Double,
        puntoRecoleccion: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrando = true, error = null) }

            val usuarioId = userPreferences.getUserId()
            if (usuarioId.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isRegistrando = false,
                        error = "Usuario no autenticado"
                    )
                }
                return@launch
            }

            // Si tu repository necesita contenedorId, puedes pasarlo como parámetro adicional
            // Por ahora uso un valor por defecto
            val contenedorId = "default-container"

            // ✅ CORREGIDO: When exhaustivo
            when (val result = reciclajeRepository.registrarReciclaje(
                usuarioId = usuarioId,
                material = material,
                pesoKg = pesoKg,
                ubicacion = puntoRecoleccion,
                contenedorId = contenedorId
            )) {
                is Result.Success<*> -> {
                    _uiState.update {
                        it.copy(
                            isRegistrando = false,
                            registroExitoso = true,
                            error = null
                        )
                    }
                    // Recargar lista de reciclajes
                    cargarReciclajes()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isRegistrando = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Ya está en isRegistrando
                }
                else -> {
                    // Caso por defecto
                    _uiState.update {
                        it.copy(
                            isRegistrando = false,
                            error = "Estado desconocido"
                        )
                    }
                }
            }
        }
    }

    fun validarConIA(imageBase64: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isValidando = true, error = null) }

            // ✅ CORREGIDO: When exhaustivo
            when (val result = reciclajeRepository.validarConIA(imageBase64)) {
                is Result.Success<*> -> {
                    _uiState.update {
                        it.copy(
                            validacionIA = result.data,
                            isValidando = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isValidando = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Ya está en isValidando
                }
                else -> {
                    // Caso por defecto
                    _uiState.update {
                        it.copy(
                            isValidando = false,
                            error = "Estado desconocido"
                        )
                    }
                }
            }
        }
    }

    fun resetRegistroExitoso() {
        _uiState.update { it.copy(registroExitoso = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun limpiarValidacionIA() {
        _uiState.update { it.copy(validacionIA = null) }
    }

    fun refresh() {
        cargarReciclajes()
    }
}

data class ReciclajesUiState(
    val reciclajes: List<Reciclaje> = emptyList(),
    val tiposMateriales: List<String> = emptyList(),
    val validacionIA: Map<String, Any>? = null,
    val isLoading: Boolean = false,
    val isRegistrando: Boolean = false,
    val isValidando: Boolean = false,
    val registroExitoso: Boolean = false,
    val error: String? = null
)