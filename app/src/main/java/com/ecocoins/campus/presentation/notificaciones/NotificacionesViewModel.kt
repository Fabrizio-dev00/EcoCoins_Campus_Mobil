package com.ecocoins.campus.presentation.notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.Notificacion
import com.ecocoins.campus.data.model.TipoNotificacion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    // TODO: Inyectar repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificacionesUiState())
    val uiState: StateFlow<NotificacionesUiState> = _uiState.asStateFlow()

    fun loadNotificaciones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                delay(500)

                // MOCK DATA
                val mockNotificaciones = listOf(
                    Notificacion(
                        id = "1",
                        titulo = "¡Canje listo!",
                        mensaje = "Tu canje de 'Café Gratis' está listo para recoger",
                        tipo = TipoNotificacion.CANJE_LISTO,
                        fecha = "2024-11-27T10:30:00",
                        leida = false
                    ),
                    Notificacion(
                        id = "2",
                        titulo = "🏆 ¡Logro desbloqueado!",
                        mensaje = "Has desbloqueado 'Reciclador Junior' - +100 EcoCoins",
                        tipo = TipoNotificacion.LOGRO_DESBLOQUEADO,
                        fecha = "2024-11-27T09:15:00",
                        leida = false
                    ),
                    Notificacion(
                        id = "3",
                        titulo = "Nueva recompensa disponible",
                        mensaje = "¡Nuevo! Descuento 20% en cafetería universitaria",
                        tipo = TipoNotificacion.NUEVA_RECOMPENSA,
                        fecha = "2024-11-26T18:00:00",
                        leida = true
                    ),
                    Notificacion(
                        id = "4",
                        titulo = "Recordatorio de reciclaje",
                        mensaje = "¡No olvides reciclar hoy! Mantén tu racha activa",
                        tipo = TipoNotificacion.RECORDATORIO,
                        fecha = "2024-11-26T08:00:00",
                        leida = true
                    ),
                    Notificacion(
                        id = "5",
                        titulo = "¡Nuevo referido!",
                        mensaje = "Juan Pérez se unió con tu código - ¡Ganaste 50 EcoCoins!",
                        tipo = TipoNotificacion.SOCIAL,
                        fecha = "2024-11-25T14:20:00",
                        leida = true
                    )
                )

                val noLeidas = mockNotificaciones.count { !it.leida }

                _uiState.update {
                    it.copy(
                        notificaciones = mockNotificaciones,
                        noLeidas = noLeidas,
                        total = mockNotificaciones.size,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar notificaciones",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun marcarLeida(id: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val notificaciones = state.notificaciones.map {
                    if (it.id == id) it.copy(leida = true) else it
                }
                val noLeidas = notificaciones.count { !it.leida }

                state.copy(
                    notificaciones = notificaciones,
                    noLeidas = noLeidas
                )
            }
        }
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    notificaciones = state.notificaciones.map { it.copy(leida = true) },
                    noLeidas = 0
                )
            }
        }
    }
}

data class NotificacionesUiState(
    val notificaciones: List<Notificacion> = emptyList(),
    val noLeidas: Int = 0,
    val total: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)