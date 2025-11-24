package com.ecocoins.campus.presentation.reciclajes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.Reciclaje
import com.ecocoins.campus.data.repository.ReciclajeRepository
import com.ecocoins.campus.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReciclajesUiState(
    val isLoading: Boolean = false,
    val reciclajes: List<Reciclaje> = emptyList(),
    val tiposMateriales: List<String> = emptyList(),
    val registroExitoso: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReciclajesViewModel @Inject constructor(
    private val reciclajeRepository: ReciclajeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReciclajesUiState())
    val uiState: StateFlow<ReciclajesUiState> = _uiState.asStateFlow()

    init {
        loadReciclajes()
        loadTiposMateriales()
    }

    private fun loadReciclajes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = reciclajeRepository.getUserReciclajes()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reciclajes = result.data
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

    private fun loadTiposMateriales() {
        _uiState.update {
            it.copy(tiposMateriales = reciclajeRepository.getTiposMateriales())
        }
    }

    fun registrarReciclaje(tipoMaterial: String, pesoKg: Double, puntoRecoleccion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, registroExitoso = false) }

            when (val result = reciclajeRepository.registrarReciclaje(
                tipoMaterial,
                pesoKg,
                puntoRecoleccion
            )) {
                is Result.Success -> {
                    loadReciclajes()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registroExitoso = true
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

    fun resetRegistroExitoso() {
        _uiState.update { it.copy(registroExitoso = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}