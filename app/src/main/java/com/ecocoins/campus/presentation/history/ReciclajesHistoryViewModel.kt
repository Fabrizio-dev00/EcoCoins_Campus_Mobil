package com.ecocoins.campus.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.repository.ReciclajeRepository
import com.ecocoins.campus.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReciclajesHistoryViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReciclajesHistoryUiState())
    val uiState: StateFlow<ReciclajesHistoryUiState> = _uiState.asStateFlow()

    init {
        loadReciclajes()
    }

    private fun loadReciclajes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = reciclajeRepository.getUserReciclajes()) {
                is Result.Success -> {
                    val reciclajes = result.data

                    // Calcular totales
                    val totalKg = reciclajes.sumOf { it.pesoKg }
                    val totalEcoCoins = reciclajes.sumOf { it.ecoCoinsGanadas }

                    _uiState.update {
                        it.copy(
                            reciclajes = reciclajes.sortedByDescending { r -> r.fecha },
                            totalKgReciclados = totalKg,
                            totalEcoCoins = totalEcoCoins,
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
                else -> {}
            }
        }
    }

    fun refresh() {
        loadReciclajes()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ReciclajesHistoryUiState(
    val reciclajes: List<Reciclaje> = emptyList(),
    val totalKgReciclados: Double = 0.0,
    val totalEcoCoins: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)