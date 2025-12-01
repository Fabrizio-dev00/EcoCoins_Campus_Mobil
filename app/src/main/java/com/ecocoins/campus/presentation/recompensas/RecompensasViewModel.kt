package com.ecocoins.campus.presentation.recompensas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.utils.Result
import com.ecocoins.campus.data.repository.RecompensasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecompensasViewModel @Inject constructor(
    private val recompensasRepository: RecompensasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecompensasUiState())
    val uiState: StateFlow<RecompensasUiState> = _uiState.asStateFlow()

    init {
        cargarRecompensas()
        cargarCanjes()
    }

    private fun cargarRecompensas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = recompensasRepository.obtenerRecompensas()) {
                is Result.Success -> {
                    // ✅ CORREGIDO: result.data ya es List<Recompensa>, no nullable
                    _uiState.update {
                        it.copy(
                            recompensas = result.data,
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

    fun cargarRecompensa(recompensaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = recompensasRepository.obtenerRecompensa(recompensaId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            recompensaSeleccionada = result.data,
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

    fun canjearRecompensa(
        recompensaId: String,
        direccion: String? = null,
        telefono: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCanjeando = true, error = null) }

            val usuarioId = userPreferences.getUserId()
            if (usuarioId.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        isCanjeando = false,
                        error = "Usuario no autenticado"
                    )
                }
                return@launch
            }

            when (val result = recompensasRepository.canjearRecompensa(
                usuarioId = usuarioId,
                recompensaId = recompensaId
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isCanjeando = false,
                            canjeExitoso = true,
                            canjeMessage = "¡Recompensa canjeada exitosamente!",
                            error = null
                        )
                    }
                    // Recargar listas
                    cargarRecompensas()
                    cargarCanjes()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isCanjeando = false,
                            error = result.message
                        )
                    }
                }
                Result.Loading -> {
                    // Ya está en isCanjeando
                }
            }
        }
    }

    private fun cargarCanjes(estado: String? = null) {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = recompensasRepository.obtenerCanjesUsuario(usuarioId, estado)) {
                is Result.Success -> {
                    // ✅ CORREGIDO: result.data ya es List<Canje>
                    _uiState.update {
                        it.copy(canjes = result.data)
                    }
                }
                is Result.Error -> {
                    // Error silencioso al cargar canjes (no es crítico)
                }
                Result.Loading -> {
                    // No mostramos loading para canjes (es secundario)
                }
            }
        }
    }

    fun cargarCanje(canjeId: String) {
        viewModelScope.launch {
            when (val result = recompensasRepository.obtenerCanje(canjeId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(canjeSeleccionado = result.data)
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message)
                    }
                }
                Result.Loading -> {
                    // Loading ya manejado
                }
            }
        }
    }

    // ✅ CORREGIDO: getUser() es suspending, debe llamarse en viewModelScope
    fun filtrarRecompensasDisponibles() {
        viewModelScope.launch {
            val todasLasRecompensas = _uiState.value.recompensas
            val user = userPreferences.getUser()  // ✅ Ahora está dentro de launch
            val ecoCoinsUsuario = user?.ecoCoins ?: 0

            val recompensasDisponibles = todasLasRecompensas.filter {
                it.disponible && it.stock > 0 && it.costoEcocoins <= ecoCoinsUsuario
            }

            // Actualizar estado con recompensas filtradas si lo necesitas
            // O simplemente usar para mostrar en UI
        }
    }

    fun filtrarCanjesPorEstado(estado: String): List<Canje> {
        return _uiState.value.canjes.filter { it.estado == estado }
    }

    fun resetCanjeExitoso() {
        _uiState.update {
            it.copy(canjeExitoso = false, canjeMessage = null)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refresh() {
        cargarRecompensas()
        cargarCanjes()
    }
}

data class RecompensasUiState(
    val recompensas: List<Recompensa> = emptyList(),
    val recompensaSeleccionada: Recompensa? = null,
    val canjes: List<Canje> = emptyList(),
    val canjeSeleccionado: Canje? = null,
    val isLoading: Boolean = false,
    val isCanjeando: Boolean = false,
    val canjeExitoso: Boolean = false,
    val canjeMessage: String? = null,
    val error: String? = null
)