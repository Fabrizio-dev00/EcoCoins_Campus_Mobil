package com.ecocoins.campus.presentation.logros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.CategoriaLogro
import com.ecocoins.campus.data.model.Logro
import com.ecocoins.campus.data.model.RarezaLogro
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogrosViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté disponible
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogrosUiState())
    val uiState: StateFlow<LogrosUiState> = _uiState.asStateFlow()

    fun loadLogros() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // TODO: Llamar al repositorio
                // val logros = logrosRepository.getLogros()

                // MOCK DATA - Reemplazar con datos reales
                delay(800)

                val mockLogros = listOf(
                    // RECICLAJE
                    Logro(
                        id = "1",
                        nombre = "Primer Paso",
                        descripcion = "Realiza tu primer reciclaje",
                        icono = "♻️",
                        categoria = CategoriaLogro.RECICLAJE,
                        objetivo = 1,
                        progreso = 1,
                        desbloqueado = true,
                        fechaDesbloqueo = "15/11/2024",
                        recompensaEcoCoins = 50,
                        rareza = RarezaLogro.COMUN
                    ),
                    Logro(
                        id = "2",
                        nombre = "Reciclador Junior",
                        descripcion = "Completa 10 reciclajes",
                        icono = "♻️",
                        categoria = CategoriaLogro.RECICLAJE,
                        objetivo = 10,
                        progreso = 8,
                        desbloqueado = false,
                        recompensaEcoCoins = 100,
                        rareza = RarezaLogro.COMUN
                    ),
                    Logro(
                        id = "3",
                        nombre = "Experto Reciclador",
                        descripcion = "Completa 50 reciclajes",
                        icono = "♻️",
                        categoria = CategoriaLogro.RECICLAJE,
                        objetivo = 50,
                        progreso = 8,
                        desbloqueado = false,
                        recompensaEcoCoins = 500,
                        rareza = RarezaLogro.RARO
                    ),
                    Logro(
                        id = "4",
                        nombre = "Maestro del Reciclaje",
                        descripcion = "Completa 100 reciclajes",
                        icono = "♻️",
                        categoria = CategoriaLogro.RECICLAJE,
                        objetivo = 100,
                        progreso = 8,
                        desbloqueado = false,
                        recompensaEcoCoins = 1000,
                        rareza = RarezaLogro.EPICO
                    ),

                    // ECOCOINS
                    Logro(
                        id = "5",
                        nombre = "Primer Ahorro",
                        descripcion = "Acumula 100 EcoCoins",
                        icono = "💰",
                        categoria = CategoriaLogro.ECOCOINS,
                        objetivo = 100,
                        progreso = 100,
                        desbloqueado = true,
                        fechaDesbloqueo = "18/11/2024",
                        recompensaEcoCoins = 50,
                        rareza = RarezaLogro.COMUN
                    ),
                    Logro(
                        id = "6",
                        nombre = "Millonario Verde",
                        descripcion = "Acumula 1000 EcoCoins",
                        icono = "💰",
                        categoria = CategoriaLogro.ECOCOINS,
                        objetivo = 1000,
                        progreso = 550,
                        desbloqueado = false,
                        recompensaEcoCoins = 200,
                        rareza = RarezaLogro.RARO
                    ),
                    Logro(
                        id = "7",
                        nombre = "Magnate Ecológico",
                        descripcion = "Acumula 5000 EcoCoins",
                        icono = "💰",
                        categoria = CategoriaLogro.ECOCOINS,
                        objetivo = 5000,
                        progreso = 550,
                        desbloqueado = false,
                        recompensaEcoCoins = 1000,
                        rareza = RarezaLogro.EPICO
                    ),

                    // RACHAS
                    Logro(
                        id = "8",
                        nombre = "Racha Semanal",
                        descripcion = "Recicla 7 días seguidos",
                        icono = "🔥",
                        categoria = CategoriaLogro.RACHA,
                        objetivo = 7,
                        progreso = 3,
                        desbloqueado = false,
                        recompensaEcoCoins = 150,
                        rareza = RarezaLogro.RARO
                    ),
                    Logro(
                        id = "9",
                        nombre = "Racha Mensual",
                        descripcion = "Recicla 30 días seguidos",
                        icono = "🔥",
                        categoria = CategoriaLogro.RACHA,
                        objetivo = 30,
                        progreso = 3,
                        desbloqueado = false,
                        recompensaEcoCoins = 500,
                        rareza = RarezaLogro.EPICO
                    ),
                    Logro(
                        id = "10",
                        nombre = "Imparable",
                        descripcion = "Recicla 100 días seguidos",
                        icono = "🔥",
                        categoria = CategoriaLogro.RACHA,
                        objetivo = 100,
                        progreso = 3,
                        desbloqueado = false,
                        recompensaEcoCoins = 2000,
                        rareza = RarezaLogro.LEGENDARIO
                    ),

                    // SOCIAL
                    Logro(
                        id = "11",
                        nombre = "Influencer Verde",
                        descripcion = "Refiere 5 amigos",
                        icono = "👥",
                        categoria = CategoriaLogro.SOCIAL,
                        objetivo = 5,
                        progreso = 0,
                        desbloqueado = false,
                        recompensaEcoCoins = 250,
                        rareza = RarezaLogro.RARO
                    ),
                    Logro(
                        id = "12",
                        nombre = "Líder Comunitario",
                        descripcion = "Refiere 20 amigos",
                        icono = "👥",
                        categoria = CategoriaLogro.SOCIAL,
                        objetivo = 20,
                        progreso = 0,
                        desbloqueado = false,
                        recompensaEcoCoins = 1000,
                        rareza = RarezaLogro.EPICO
                    ),

                    // ESPECIALES
                    Logro(
                        id = "13",
                        nombre = "Top 10",
                        descripcion = "Entra al top 10 del ranking",
                        icono = "⭐",
                        categoria = CategoriaLogro.ESPECIAL,
                        objetivo = 1,
                        progreso = 0,
                        desbloqueado = false,
                        recompensaEcoCoins = 500,
                        rareza = RarezaLogro.EPICO
                    ),
                    Logro(
                        id = "14",
                        nombre = "Campeón",
                        descripcion = "Alcanza el puesto #1 del ranking",
                        icono = "⭐",
                        categoria = CategoriaLogro.ESPECIAL,
                        objetivo = 1,
                        progreso = 0,
                        desbloqueado = false,
                        recompensaEcoCoins = 2000,
                        rareza = RarezaLogro.LEGENDARIO
                    ),
                    Logro(
                        id = "15",
                        nombre = "Diversidad",
                        descripcion = "Recicla los 4 tipos de materiales",
                        icono = "⭐",
                        categoria = CategoriaLogro.ESPECIAL,
                        objetivo = 4,
                        progreso = 2,
                        desbloqueado = false,
                        recompensaEcoCoins = 300,
                        rareza = RarezaLogro.RARO
                    )
                )

                val totalDesbloqueados = mockLogros.count { it.desbloqueado }
                val porcentaje = (totalDesbloqueados * 100) / mockLogros.size

                _uiState.update {
                    it.copy(
                        logros = mockLogros,
                        totalDesbloqueados = totalDesbloqueados,
                        totalLogros = mockLogros.size,
                        porcentajeCompletado = porcentaje,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar logros: ${e.message}",
                        isLoading = false
                    )
                }
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

data class LogrosUiState(
    val logros: List<Logro> = emptyList(),
    val totalDesbloqueados: Int = 0,
    val totalLogros: Int = 0,
    val porcentajeCompletado: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)