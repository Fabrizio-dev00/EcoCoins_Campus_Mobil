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
    val estadisticas: EstadisticasGenerales? = null,
    val ranking: List<UserRanking> = emptyList(),
    val materialesMasReciclados: List<MaterialStat> = emptyList(),
    val userMaterials: List<Material> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val estadisticasRepository: EstadisticasRepository,
    private val materialRepository: MaterialRepository
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
            when (val statsResult = estadisticasRepository.getEstadisticasGenerales()) {
                is Result.Success -> {
                    _uiState.update { it.copy(estadisticas = statsResult.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = statsResult.message) }
                }
                else -> {}
            }

            // Cargar ranking de usuarios
            when (val rankingResult = estadisticasRepository.getRankingUsuarios(10)) {
                is Result.Success -> {
                    _uiState.update { it.copy(ranking = rankingResult.data) }
                }
                is Result.Error -> {}
                else -> {}
            }

            // Cargar materiales del usuario
            when (val materialsResult = materialRepository.getUserMaterials()) {
                is Result.Success -> {
                    _uiState.update { it.copy(userMaterials = materialsResult.data) }
                }
                is Result.Error -> {}
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
}