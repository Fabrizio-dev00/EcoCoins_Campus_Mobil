package com.ecocoins.campus.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.model.*
import com.ecocoins.campus.data.repository.*
import com.ecocoins.campus.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val reciclajes: List<Reciclaje> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
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

            // Cargar perfil
            when (val userResult = authRepository.getUserProfile()) {
                is Result.Success -> {
                    _uiState.update { it.copy(user = userResult.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = userResult.message) }
                }
                else -> {}
            }

            // Cargar reciclajes
            when (val reciclajesResult = reciclajeRepository.getUserReciclajes()) {
                is Result.Success -> {
                    _uiState.update { it.copy(reciclajes = reciclajesResult.data) }
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