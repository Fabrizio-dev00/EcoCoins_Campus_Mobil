package com.ecocoins.campus.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.User
import com.ecocoins.campus.data.repository.AuthRepository
import com.ecocoins.campus.data.repository.EstadisticasRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val estadisticasRepository: EstadisticasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // ✅ CAMBIO: De ResumenGeneral a Map<String, Any>
    private val _resumenEstadisticas = MutableLiveData<Resource<Map<String, Any>>>()
    val resumenEstadisticas: LiveData<Resource<Map<String, Any>>> = _resumenEstadisticas

    private val _ecoCoins = MutableLiveData<Long>()
    val ecoCoins: LiveData<Long> = _ecoCoins

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()
            val userName = userPreferences.userName.firstOrNull()
            val userEmail = userPreferences.userEmail.firstOrNull()
            val ecoCoinsValue = userPreferences.userEcoCoins.firstOrNull() ?: 0L

            _ecoCoins.value = ecoCoinsValue

            if (userId != null) {
                // Cargar perfil del usuario
                authRepository.getPerfil(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _user.value = resource.data
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                        }
                        is Resource.Loading -> {}
                    }
                }

                // Cargar resumen de estadísticas
                estadisticasRepository.getResumenEstadisticas(userId).collect { resource ->
                    _resumenEstadisticas.value = resource
                    when (resource) {
                        is Resource.Success -> {
                            _isLoading.value = false
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            _error.value = resource.message
                        }
                        is Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } else {
                _isLoading.value = false
                _error.value = "Usuario no autenticado"
            }
        }
    }

    fun refreshData() {
        loadDashboardData()
    }

    fun clearError() {
        _error.value = null
    }
}
