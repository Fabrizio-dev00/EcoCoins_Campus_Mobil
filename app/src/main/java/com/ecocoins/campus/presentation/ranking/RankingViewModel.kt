package com.ecocoins.campus.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.RankingUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté disponible
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    fun loadRanking(periodo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // TODO: Llamar al repositorio
                // val ranking = rankingRepository.getRanking(periodo)

                // MOCK DATA - Reemplazar con datos reales
                delay(800)

                val mockUsuarios = listOf(
                    RankingUsuario("1", "Ana García", "Ing. Sistemas", 2500, 45, 125.5, 3, 1),
                    RankingUsuario("2", "Carlos Ruiz", "Ing. Ambiental", 2300, 42, 118.0, 3, 2),
                    RankingUsuario("3", "María López", "Ing. Civil", 2100, 38, 105.2, 2, 3),
                    RankingUsuario("4", "Pedro Sánchez", "Arquitectura", 1950, 35, 98.5, 2, 4),
                    RankingUsuario("5", "Laura Martínez", "Ing. Industrial", 1850, 33, 92.0, 2, 5),
                    RankingUsuario("6", "Diego Torres", "Ing. Mecánica", 1750, 31, 87.5, 2, 6),
                    RankingUsuario("7", "Sofia Ramírez", "Ing. Química", 1650, 29, 82.0, 2, 7),
                    RankingUsuario("8", "Juan Pérez", "Ing. Eléctrica", 1550, 27, 76.5, 1, 8),
                    RankingUsuario("9", "Carmen Díaz", "Biología", 1450, 25, 71.0, 1, 9),
                    RankingUsuario("10", "Roberto Silva", "Medicina", 1350, 23, 65.5, 1, 10)
                )

                _uiState.update {
                    it.copy(
                        topUsuarios = mockUsuarios,
                        miPosicion = 8, // Usuario actual en posición 8
                        miPuntos = 1550,
                        totalUsuarios = 150,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar el ranking: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refresh(periodo: String) {
        loadRanking(periodo)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class RankingUiState(
    val topUsuarios: List<RankingUsuario> = emptyList(),
    val miPosicion: Int = 0,
    val miPuntos: Int = 0,
    val totalUsuarios: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)