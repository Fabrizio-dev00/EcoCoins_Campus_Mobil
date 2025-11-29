package com.ecocoins.campus.presentation.estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.EstadisticaMaterial
import com.ecocoins.campus.data.model.DatoTendencia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté disponible
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstadisticasUiState())
    val uiState: StateFlow<EstadisticasUiState> = _uiState.asStateFlow()

    fun loadEstadisticas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // TODO: Llamar al repositorio
                // val estadisticas = estadisticasRepository.getEstadisticasDetalladas()

                // MOCK DATA - Reemplazar con datos reales
                delay(800)

                val mockMateriales = listOf(
                    EstadisticaMaterial("Plástico", 15, 12.5, 375, 35f, "#2196F3"),
                    EstadisticaMaterial("Papel", 12, 18.0, 360, 30f, "#8D6E63"),
                    EstadisticaMaterial("Vidrio", 8, 24.0, 320, 20f, "#4CAF50"),
                    EstadisticaMaterial("Metal", 5, 8.5, 200, 15f, "#9E9E9E")
                )

                val mockTendencias = listOf(
                    DatoTendencia("21/11", 3, 4.5, 90),
                    DatoTendencia("22/11", 2, 3.2, 64),
                    DatoTendencia("23/11", 4, 6.1, 122),
                    DatoTendencia("24/11", 1, 1.5, 30),
                    DatoTendencia("25/11", 5, 7.8, 156),
                    DatoTendencia("26/11", 3, 4.2, 84),
                    DatoTendencia("27/11", 2, 3.0, 60)
                )

                _uiState.update {
                    it.copy(
                        // Resumen General
                        totalReciclajes = 40,
                        totalKgReciclados = 63.0,
                        totalEcoCoinsGanados = 1255,
                        totalEcoCoinsGastados = 700,
                        saldoActual = 555,

                        // Por Material
                        porTipoMaterial = mockMateriales,

                        // Tendencia
                        tendenciaSemanal = mockTendencias,

                        // Impacto Ambiental
                        co2Ahorrado = 157.5, // kg * 2.5
                        arbolesEquivalentes = 6, // kg / 10
                        energiaAhorrada = 189.0, // kg * 3
                        aguaAhorrada = 3150.0, // kg * 50

                        // Comparativas
                        promedioUniversidad = 45.0,
                        tuRendimiento = 63.0,
                        mejorQuePorc = 72,
                        posicionGeneral = 8,

                        // Rachas
                        rachaActual = 3,
                        mejorRacha = 7,
                        diasTotales = 25,
                        ultimoReciclaje = "Hoy, 10:30 AM",

                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar estadísticas: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refresh() {
        loadEstadisticas()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class EstadisticasUiState(
    // Resumen General
    val totalReciclajes: Int = 0,
    val totalKgReciclados: Double = 0.0,
    val totalEcoCoinsGanados: Int = 0,
    val totalEcoCoinsGastados: Int = 0,
    val saldoActual: Int = 0,

    // Por Material
    val porTipoMaterial: List<EstadisticaMaterial> = emptyList(),

    // Tendencia
    val tendenciaSemanal: List<DatoTendencia> = emptyList(),

    // Impacto Ambiental
    val co2Ahorrado: Double = 0.0,
    val arbolesEquivalentes: Int = 0,
    val energiaAhorrada: Double = 0.0,
    val aguaAhorrada: Double = 0.0,

    // Comparativas
    val promedioUniversidad: Double = 0.0,
    val tuRendimiento: Double = 0.0,
    val mejorQuePorc: Int = 0,
    val posicionGeneral: Int = 0,

    // Rachas
    val rachaActual: Int = 0,
    val mejorRacha: Int = 0,
    val diasTotales: Int = 0,
    val ultimoReciclaje: String? = null,

    val isLoading: Boolean = false,
    val error: String? = null
)