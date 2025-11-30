package com.ecocoins.campus.presentation.estadisticas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocoins.campus.data.local.UserPreferences
import com.ecocoins.campus.data.model.EstadisticasDetalladas
import com.ecocoins.campus.data.model.Resource
import com.ecocoins.campus.data.repository.EstadisticasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject  // ✅ CAMBIO: javax en lugar de jakarta
import kotlinx.coroutines.launch

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val estadisticasRepository: EstadisticasRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _estadisticas = MutableLiveData<Resource<EstadisticasDetalladas>>()
    val estadisticas: LiveData<Resource<EstadisticasDetalladas>> = _estadisticas

    init {
        cargarEstadisticas()
    }

    fun cargarEstadisticas() {
        viewModelScope.launch {
            _estadisticas.value = Resource.Loading()
            val usuarioId = userPreferences.getUserId() ?: return@launch

            when (val result = estadisticasRepository.obtenerEstadisticasCompletas(usuarioId)) {
                is Resource.Success -> {
                    _estadisticas.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _estadisticas.value = Resource.Error(
                        result.message ?: "Error al cargar estadísticas"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        cargarEstadisticas()
    }
}