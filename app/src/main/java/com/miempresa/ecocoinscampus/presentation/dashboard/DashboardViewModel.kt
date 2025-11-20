package com.miempresa.ecocoinscampus.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miempresa.ecocoinscampus.data.model.*
import com.miempresa.ecocoinscampus.data.repository.*
import com.miempresa.ecocoinscampus.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val estadisticas: Estadisticas? = null,
    val userReciclajes: List<Reciclaje> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val estadisticasRepository: EstadisticasRepository,
    private val reciclajeRepository: ReciclajeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Cargar perfil de usuario
            when (val userResult = authRepository.getUserProfile()) {
                is Result.Success -> {
                    _uiState.update { it.copy(user = userResult.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = userResult.message) }
                }
                else -> {}
            }

            // Cargar estadísticas generales
            when (val statsResult = estadisticasRepository.getEstadisticas()) {
                is Result.Success -> {
                    _uiState.update { it.copy(estadisticas = statsResult.data) }
                }
                is Result.Error -> {
                    // No mostrar error si las estadísticas fallan
                }
                else -> {}
            }

            // Cargar reciclajes del usuario
            when (val reciclajesResult = reciclajeRepository.getUserReciclajes()) {
                is Result.Success -> {
                    _uiState.update { it.copy(userReciclajes = reciclajesResult.data) }
                }
                is Result.Error -> {
                    // No mostrar error si los reciclajes fallan
                }
                else -> {}
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refreshUserProfile() {
        viewModelScope.launch {
            when (val result = authRepository.getUserProfile()) {
                is Result.Success -> {
                    _uiState.update { it.copy(user = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}