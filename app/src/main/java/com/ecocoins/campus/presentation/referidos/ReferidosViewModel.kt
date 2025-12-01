package com.ecocoins.campus.presentation.referidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.utils.Result
import com.ecocoins.campus.data.repository.ReferidosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferidosViewModel @Inject constructor(
    private val referidosRepository: ReferidosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReferidosUiState())
    val uiState: StateFlow<ReferidosUiState> = _uiState.asStateFlow()

    init {
        loadReferidos()
    }

    fun loadReferidos() {
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

            when (val result = referidosRepository.obtenerReferidos(usuarioId)) {
                is Result.Success -> {
                    val info = result.data

                    // ✅ Calcular totalEcoCoinsGanados si no existe
                    val totalEcoCoins = try {
                        // Intenta usar la propiedad si existe
                        info.totalEcoCoinsGanados
                    } catch (e: Exception) {
                        // Si no existe, calcular: 50 EcoCoins por referido
                        info.totalReferidos * 50
                    }

                    _uiState.update {
                        it.copy(
                            codigoReferido = info.codigoReferido,
                            totalReferidos = info.totalReferidos,
                            totalEcoCoinsGanados = totalEcoCoins,
                            referidos = info.referidos.mapNotNull { ref ->
                                try {
                                    ReferidoItem(
                                        id = ref.id, // Si falla, usar el catch
                                        nombre = ref.nombre,
                                        fechaRegistro = ref.fechaRegistro.take(10)
                                    )
                                } catch (e: Exception) {
                                    null // Ignorar referidos con datos incompletos
                                }
                            },
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
                Result.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    fun generarCodigo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val usuarioId = userPreferences.getUserId()
            val user = userPreferences.getUser()
            val nombre = user?.nombre

            if (usuarioId.isNullOrEmpty() || nombre.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Usuario no autenticado"
                    )
                }
                return@launch
            }

            when (val result = referidosRepository.generarCodigo(usuarioId, nombre)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            codigoReferido = result.data.codigo,
                            isLoading = false,
                            error = null
                        )
                    }
                    loadReferidos()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                Result.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    fun validarCodigo(codigo: String, onValidado: (Boolean) -> Unit) {
        viewModelScope.launch {
            when (val result = referidosRepository.validarCodigo(codigo)) {
                is Result.Success -> {
                    onValidado(true)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message)
                    }
                    onValidado(false)
                }
                Result.Loading -> {
                    // Loading
                }
            }
        }
    }

    fun compartirCodigo(): String {
        val codigo = _uiState.value.codigoReferido

        return "¡Únete a EcoCoins Campus! 🌱♻️\n\n" +
                "Usa mi código de referido: $codigo\n\n" +
                "Recicla, gana EcoCoins y canjea premios increíbles. " +
                "¡Juntos hacemos la diferencia! 🌍💚"
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refresh() {
        loadReferidos()
    }
}

data class ReferidosUiState(
    val codigoReferido: String = "",
    val totalReferidos: Int = 0,
    val totalEcoCoinsGanados: Int = 0,
    val referidos: List<ReferidoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ReferidoItem(
    val id: String,
    val nombre: String,
    val fechaRegistro: String
)