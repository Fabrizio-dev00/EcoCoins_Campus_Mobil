package com.ecocoins.campus.presentation.estadisticas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.EstadisticasDetalladas
import com.ecocoins.campus.data.repository.EstadisticasRepository
import com.ecocoins.campus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val estadisticasRepository: EstadisticasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _estadisticas = MutableLiveData<EstadisticasDetalladas?>()
    val estadisticas: LiveData<EstadisticasDetalladas?> = _estadisticas

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadEstadisticas()
    }

    fun loadEstadisticas() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = userPreferences.userId.firstOrNull()

            if (userId != null) {
                estadisticasRepository.getEstadisticasUsuario(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _estadisticas.value = resource.data
                            _isLoading.value = false
                        }
                        is Resource.Error -> {
                            _error.value = resource.message
                            _isLoading.value = false
                        }
                        is Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } else {
                _error.value = "Usuario no autenticado"
                _isLoading.value = false
            }
        }
    }

    fun refreshEstadisticas() {
        loadEstadisticas()
    }

    fun clearError() {
        _error.value = null
    }
}