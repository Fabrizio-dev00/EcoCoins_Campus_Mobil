package com.ecocoins.campus.presentation.notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Notificacion
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.NotificacionesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val notificacionesRepository: NotificacionesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // ✅ CORREGIDO: Usar StateFlow en lugar de LiveData
    private val _uiState = MutableStateFlow(NotificacionesUiState())
    val uiState: StateFlow<NotificacionesUiState> = _uiState.asStateFlow()

    init {
        loadNotificaciones()
    }

    // ✅ CORREGIDO: Renombrado de cargarNotificaciones() a loadNotificaciones()
    fun loadNotificaciones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // ✅ CORREGIDO: getUserId() es suspending
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

            when (val result = notificacionesRepository.obtenerNotificaciones(usuarioId)) {
                is Resource.Success -> {
                    val notificaciones = result.data ?: emptyList()
                    val noLeidas = notificaciones.count { !it.leida }

                    _uiState.update {
                        it.copy(
                            notificaciones = notificaciones,
                            noLeidas = noLeidas,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al cargar notificaciones"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Ya manejado con isLoading
                }
            }
        }
    }

    // ✅ CORREGIDO: Renombrado para coincidir con el Screen
    fun marcarLeida(notificacionId: String) {
        viewModelScope.launch {
            when (notificacionesRepository.marcarComoLeida(notificacionId)) {
                is Resource.Success -> {
                    // Actualizar localmente sin recargar todo
                    _uiState.update { currentState ->
                        val notificacionesActualizadas = currentState.notificaciones.map { notif ->
                            if (notif.id == notificacionId) {
                                notif.copy(leida = true)
                            } else {
                                notif
                            }
                        }
                        val noLeidas = notificacionesActualizadas.count { !it.leida }

                        currentState.copy(
                            notificaciones = notificacionesActualizadas,
                            noLeidas = noLeidas
                        )
                    }
                }
                is Resource.Error -> {
                    // Error silencioso
                }
                is Resource.Loading -> {}
            }
        }
    }

    // ✅ CORREGIDO: Renombrado para coincidir con el Screen
    fun marcarTodasLeidas() {
        viewModelScope.launch {
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (notificacionesRepository.marcarTodasComoLeidas(usuarioId)) {
                is Resource.Success -> {
                    // Marcar todas como leídas localmente
                    _uiState.update { currentState ->
                        val notificacionesActualizadas = currentState.notificaciones.map {
                            it.copy(leida = true)
                        }

                        currentState.copy(
                            notificaciones = notificacionesActualizadas,
                            noLeidas = 0
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(error = "Error al marcar notificaciones como leídas")
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadNotificaciones()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

// ✅ AGREGADO: Clase UiState que faltaba
data class NotificacionesUiState(
    val notificaciones: List<Notificacion> = emptyList(),
    val noLeidas: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)