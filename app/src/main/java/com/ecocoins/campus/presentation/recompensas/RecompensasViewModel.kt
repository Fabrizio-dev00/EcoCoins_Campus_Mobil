package com.ecocoins.campus.presentation.recompensas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.Recompensa
import com.ecocoins.campus.data.repository.RecompensasRepository
import com.ecocoins.campus.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecompensasUiState(
    val isLoading: Boolean = false,
    val recompensas: List<Recompensa> = emptyList(),
    val canjeExitoso: Boolean = false,
    val canjeMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class RecompensasViewModel @Inject constructor(
    private val recompensasRepository: RecompensasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecompensasUiState())
    val uiState: StateFlow<RecompensasUiState> = _uiState.asStateFlow()

    init {
        loadRecompensas()
    }

    fun loadRecompensas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = recompensasRepository.getRecompensas()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recompensas = result.data
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

    fun canjearRecompensa(
        recompensaId: String,
        direccionEntrega: String? = null,
        telefonoContacto: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, canjeExitoso = false) }

            when (val result = recompensasRepository.canjearRecompensa(
                recompensaId,
                direccionEntrega,
                telefonoContacto
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            canjeExitoso = true,
                            canjeMessage = result.data.mensaje
                        )
                    }
                    loadRecompensas()
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

    fun resetCanjeExitoso() {
        _uiState.update { it.copy(canjeExitoso = false, canjeMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}