package com.ecocoins.campus.presentation.referidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferidosViewModel @Inject constructor(
    // TODO: Inyectar repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReferidosUiState())
    val uiState: StateFlow<ReferidosUiState> = _uiState.asStateFlow()

    fun loadReferidos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                delay(500)

                // MOCK DATA
                val mockReferidos = listOf(
                    ReferidoItem("1", "María García", "hace 2 días"),
                    ReferidoItem("2", "Carlos Ruiz", "hace 1 semana"),
                    ReferidoItem("3", "Ana López", "hace 2 semanas")
                )

                _uiState.update {
                    it.copy(
                        codigoReferido = "ECO2024XYZ",
                        totalReferidos = mockReferidos.size,
                        totalEcoCoinsGanados = mockReferidos.size * 50,
                        referidos = mockReferidos,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar referidos",
                        isLoading = false
                    )
                }
            }
        }
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