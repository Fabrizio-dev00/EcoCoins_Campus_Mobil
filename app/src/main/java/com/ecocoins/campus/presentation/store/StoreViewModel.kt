package com.ecocoins.campus.presentation.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.Profesor
import com.ecocoins.campus.data.repository.AuthRepository
import com.ecocoins.campus.data.repository.ProfesorRepository
import com.ecocoins.campus.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val profesorRepository: ProfesorRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        loadProfesores()
        loadUserEcoCoins()
    }

    private fun loadProfesores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = profesorRepository.getProfesoresActivos()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            profesores = result.data,
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
                is Result.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    private fun loadUserEcoCoins() {
        viewModelScope.launch {
            when (val result = authRepository.getUserProfile()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(userEcoCoins = result.data.ecoCoins)
                    }
                }
                is Result.Error -> {
                    // No hacer nada, mantener el valor por defecto
                }
                is Result.Loading -> {
                    // Ignorar
                }
            }
        }
    }

    fun canjearRecompensa(profesorId: String, recompensaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = profesorRepository.canjearRecompensa(profesorId, recompensaId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            canjeExitoso = true,
                            error = null
                        )
                    }

                    // Actualizar el balance de EcoCoins
                    _uiState.update {
                        it.copy(userEcoCoins = result.data.nuevoBalance)
                    }

                    // Ocultar mensaje de éxito después de 3 segundos
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { it.copy(canjeExitoso = false) }

                    // Recargar profesores para actualizar stock
                    loadProfesores()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refresh() {
        loadData()
    }
}

data class StoreUiState(
    val profesores: List<Profesor> = emptyList(),
    val userEcoCoins: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canjeExitoso: Boolean = false
)