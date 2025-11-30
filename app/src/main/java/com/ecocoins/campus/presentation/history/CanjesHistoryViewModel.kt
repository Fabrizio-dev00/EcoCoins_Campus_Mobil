package com.ecocoins.campus.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.Canje
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.RecompensasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CanjesHistoryViewModel @Inject constructor(
    private val recompensasRepository: RecompensasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CanjesHistoryUiState())
    val uiState: StateFlow<CanjesHistoryUiState> = _uiState.asStateFlow()

    init {
        loadCanjes()
    }

    private fun loadCanjes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val usuarioId = userPreferences.getUserId()
            if (usuarioId.isNullOrEmpty()) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no identificado") }
                return@launch
            }

            when (val result = recompensasRepository.obtenerCanjesUsuario(usuarioId)) {
                is Resource.Success -> {
                    val canjes = result.data ?: emptyList()

                    // Calcular total gastado
                    val totalGastado = canjes.sumOf { it.costoEcoCoins }

                    _uiState.update {
                        it.copy(
                            canjes = canjes.sortedByDescending { c -> c.fechaCanje },
                            totalEcoCoinsGastados = totalGastado,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun refresh() {
        loadCanjes()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class CanjesHistoryUiState(
    val canjes: List<Canje> = emptyList(),
    val totalEcoCoinsGastados: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
